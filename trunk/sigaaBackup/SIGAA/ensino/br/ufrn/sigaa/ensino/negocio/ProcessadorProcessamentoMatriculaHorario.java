/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2010
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaHorarioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MatriculaHorario;
import br.ufrn.sigaa.ensino.dominio.OpcaoHorario;
import br.ufrn.sigaa.ensino.dominio.OpcaoModulo;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.processamento.dominio.DiscenteEmProcessamento;

/**
 * Processador responsável pelo processamento da matrícula por horário.
 * 
 * @author Leonardo Campos
 *
 */
public class ProcessadorProcessamentoMatriculaHorario extends
		AbstractProcessador {

	/** 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		MovimentoMatriculaHorario movimento = (MovimentoMatriculaHorario) mov;
		CalendarioAcademico calendario = movimento.getCalendarioAcademicoAtual();
		int ano = calendario.getAno();
		int periodo = calendario.getPeriodo();
		
		MatriculaHorarioDao dao = getDAO(MatriculaHorarioDao.class, mov);
		
		try {
		
			if(mov.getCodMovimento().equals(SigaaListaComando.PROCESSAR_MATRICULA_HORARIO)){
			
				Map<Integer, MatriculaHorario> solicitacoes = dao.findSolicitacoesMatriculaHorario(ano, periodo);
				
				TreeSet<DiscenteEmProcessamento> discentes = dao.findDiscentesAProcessar(calendario.getAnoAnterior(), calendario.getPeriodoAnterior(), solicitacoes, 
										ParametroHelper.getInstance().getParametroInt(ParametrosTecnico.ID_UNIDADE_INSTITUTO_METROPOLE_DIGITAL));
				
				HashMap<Modulo, HashMap<String, List<Turma>>> ofertaModulos = dao.findOfertaModulos(ano, periodo, ParametroHelper.getInstance().getParametroIntArray(ParametrosTecnico.ID_ENFASES_MODULO_AVANCADO_METROPOLE_DIGITAL));
				
				List<Turma> turmas = getTodasTurmas(ofertaModulos);
				
				LACO_DISCENTE:
				for(DiscenteEmProcessamento d: discentes){
					MatriculaHorario mat = solicitacoes.get(d.getIdDiscente());
					System.out.println(mat.getDiscente() + " - " + d.getMediaFinal());
					
					for(OpcaoModulo opm: mat.getOpcoesModulo()){
						if (true || !isInserido(ofertaModulos, d.getIdDiscente())) {
							System.out.println(opm.getModulo().getDescricao());
							HashMap<String, List<Turma>> ofertaTurmas = ofertaModulos.get(opm.getModulo());
							for(OpcaoHorario oph: opm.getOpcoesHorario()){
								System.out.println(oph.getOpcao());
								
								if(ofertaTurmas != null){
									// Estratégia para o caso de haver choque de mais de uma turma disponivel na mesma opção de horário
									// A pré-condição é que a lista de turmas esteja agrupada pelo código da turma. Assim, todas as turmas 01
									// devem estar dispostas de forma contínua (ou ordenada) na lista, da mesma forma todas as turmas 02, e assim por diante.
	
									List<Turma> list = ofertaTurmas.get(oph.getOpcao());
	
									Collections.reverse(list);
									for(Turma t: list){
										System.out.println(t.getCodigo());
										// se extrapola a capacidade tenta na turma seguinte
										if(t.getCapacidadeAluno() > t.getQtdMatriculados() && d.getPoloDiscente() == t.getPolo().getId()) {
											Collection<Turma> turmasMesmoCodigo = getTurmasCodigoIgual(t.getCodigo(), turmas);
											for (Turma tMesmoCodigo : turmasMesmoCodigo) {
												criarMatricula(mov, mat.getDiscente(), tMesmoCodigo);
											}
											continue LACO_DISCENTE;
										} else  {
											continue;
										}
									}
								}
			
							}
						}
					}
				}
				
				return turmas;
				
			} else if (mov.getCodMovimento().equals(SigaaListaComando.PERSISTIR_MATRICULA_HORARIO)){
				
				for(Turma t: movimento.getTurmas())
					for(MatriculaComponente mc: t.getMatriculasDisciplina())
						dao.createNoFlush(mc);
				
			}

		} finally {
			dao.close();
		}

		return null;
	}

	private List<Turma> getTodasTurmas(HashMap<Modulo, HashMap<String, List<Turma>>> ofertaModulos) {
		List<Turma> turmas = new ArrayList<Turma>();			
		for(HashMap<String, List<Turma>> map: ofertaModulos.values())
			for(List<Turma> list: map.values())
				for(Turma t: list)
					turmas.add(t);
		
		Collections.sort(turmas, new Comparator<Turma>(){
			@Override
			public int compare(Turma o1, Turma o2) {
				return o1.getCodigo().compareTo(o2.getCodigo());
			}
		});
		
		return turmas;
	}

	private Collection<Turma> getTurmasCodigoIgual(final String codigo, List<Turma> turmas) {
		return CollectionUtils.select(turmas, new Predicate() {
			public boolean evaluate(Object t) {
				return codigo.equals(((Turma) t).getCodigo());
			}
		});
	}

	private boolean isInserido(HashMap<Modulo, HashMap<String, List<Turma>>> ofertaModulos, Turma t, int idDiscente) {
		for(HashMap<String, List<Turma>> map: ofertaModulos.values())
			for(List<Turma> list: map.values())
				for(Turma turmaInserida: list) {
					if (turmaInserida.getDisciplina().getId() == t.getDisciplina().getId()) {
						for (MatriculaComponente mc : turmaInserida.getMatriculasDisciplina()) {
							if (mc.getDiscente().getId() == idDiscente)
								return true;
						}
					}
				}
		return false;
	}

	private boolean isInserido(HashMap<Modulo, HashMap<String, List<Turma>>> ofertaModulos, int idDiscente) {
		for(HashMap<String, List<Turma>> map: ofertaModulos.values())
			for(List<Turma> list: map.values())
				for(Turma turmaInserida: list) {
					for (MatriculaComponente mc : turmaInserida.getMatriculasDisciplina()) {
						if (mc.getDiscente().getId() == idDiscente) {
							return true;
						}
					}
				}
		return false;
	}
	
	
	/** 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub

	}
	
	private void criarMatricula(Movimento mov, Discente discente, Turma turma) throws ArqException {
		SituacaoMatricula situacao = SituacaoMatricula.MATRICULADO;

		MatriculaComponente matricula = new MatriculaComponente();
		matricula.setDataCadastro(new Date());
		matricula.setRegistroEntrada(mov.getUsuarioLogado().getRegistroEntrada());
		matricula.setDiscente(discente);
		matricula.setTurma(turma);
		matricula.setAno((short)turma.getAno());
		matricula.setPeriodo((byte)turma.getPeriodo());
		matricula.setComponente(turma.getDisciplina());
		matricula.setDetalhesComponente(turma.getDisciplina().getDetalhes());
		matricula.setRecuperacao(null);
		matricula.setSituacaoMatricula(situacao);

		turma.addMatriculaDisciplina(matricula);
		turma.setQtdMatriculados(turma.getMatriculasDisciplina().size());
	}

}