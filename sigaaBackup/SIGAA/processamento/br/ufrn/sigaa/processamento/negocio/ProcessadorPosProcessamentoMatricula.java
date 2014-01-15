/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 06/11/2007 
 *
 */
package br.ufrn.sigaa.processamento.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dominio.MovimentoAcademico;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaGraduacaoDao;
import br.ufrn.sigaa.processamento.jsf.ProcessamentoMatriculaMBean;

/**
 * Processador para realizar o pós-processamento da matrícula em disciplinas de bloco.
 * Os alunos que tiverem sido indeferidos em uma turma do bloco são indeferidos em todas
 * as turmas do bloco.
 *
 * @author David Pereira
 *
 */
public class ProcessadorPosProcessamentoMatricula extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		ProcessamentoMatriculaGraduacaoDao dao = new ProcessamentoMatriculaGraduacaoDao();

		try {
			MovimentoAcademico amov = ((MovimentoAcademico) mov);
			Integer discente = amov.getId();
			int ano = amov.getAno();
			int periodo = amov.getPeriodo();
			boolean rematricula = amov.isRematricula();
			
			if (amov.getAcao() == ProcessamentoMatriculaMBean.POS_PROCESSAMENTO_BLOCOS) {

				List<MatriculaComponente> matriculas = dao.findMatriculasEmBlocoDiscente(discente, ano, periodo, rematricula);
				Map<Integer, Boolean> indeferido = new HashMap<Integer, Boolean>();
			
				// Pega os blocos indeferidos
				for (MatriculaComponente mat : matriculas) {
					int id = mat.getComponente().getBlocoSubUnidade().getId();
					Boolean situacao = indeferido.get(id);
					if (situacao == null)
						indeferido.put(id, false);
					
					indeferido.put(id, indeferido.get(id) || mat.getSituacaoMatricula().equals(SituacaoMatricula.INDEFERIDA));
				}
				
				// Para cada bloco indeferido, indeferir também as outras matrículas
				for (Entry<Integer, Boolean> item : indeferido.entrySet()) {
					if (item.getValue()) {
						for (MatriculaComponente mat : matriculas) {
							if (mat.getComponente().getBlocoSubUnidade().getId() == item.getKey()) {
								dao.atualizaMotivoIndeferimento(mat.getId(), "Indef. Bloco");
								dao.updateField(MatriculaComponente.class, mat.getId(), "situacaoMatricula", SituacaoMatricula.INDEFERIDA);
							}
						}
					}
				}
			} else if (amov.getAcao() == ProcessamentoMatriculaMBean.POS_PROCESSAMENTO_ATIVAR_ALUNOS_CADASTRADOS) {
				List<Discente> discentes = dao.buscarAlunosCadastradosParaAtivar(ano, periodo);
				for (Discente d : discentes) {
					DiscenteHelper.alterarStatusDiscente(d, StatusDiscente.ATIVO, amov, dao);					
				}
			} else {
				List<MatriculaComponente> matriculas = dao.findMatriculasEmCoRequisitoDiscente(discente, ano, periodo, rematricula);
				List<MatriculaComponente> matriculasOk = dao.findMatriculasDiscente(discente, ano, periodo, rematricula);
				List<Integer> indeferidos = new ArrayList<Integer>();

				// Pega os co-requisitos indeferidos
				for (MatriculaComponente mat : matriculas) {
					Collection<ComponenteCurricular> componentes = ArvoreExpressao.getMatchesComponentes(mat.getComponenteCoRequisito(), getComponentes(matriculasOk));
					if (componentes != null) {
						for (MatriculaComponente mat2 : matriculasOk) {
							if (componentes.contains(mat2.getComponente()) && mat2.getSituacaoMatricula().equals(SituacaoMatricula.INDEFERIDA)) {
								int id = mat.getId();
								indeferidos.add(id);				
							}
						}
					}
				}
				
				for (Integer item : indeferidos) {
					dao.atualizaMotivoIndeferimento(item, "Indef. Co-Requisito");
					dao.updateField(MatriculaComponente.class, item, "situacaoMatricula", SituacaoMatricula.INDEFERIDA);
				}
			}
			
			
		} finally {
			dao.close();
		}

		return null;
	}

	private List<ComponenteCurricular> getComponentes(List<MatriculaComponente> matriculas) {
		List<ComponenteCurricular> comps = new ArrayList<ComponenteCurricular>();
		for (MatriculaComponente mat : matriculas) {
			comps.add(mat.getComponente());
		}
		return comps;
	}
	
	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
