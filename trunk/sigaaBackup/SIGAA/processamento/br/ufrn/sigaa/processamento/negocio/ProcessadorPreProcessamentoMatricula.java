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

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.graduacao.ProcessamentoMatriculaDAO;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.DiscenteCalculosHelper;
import br.ufrn.sigaa.processamento.dominio.ModoProcessamentoMatricula;

/**
 * Classe responsável por:<br>
 * <ul>
 * <li>1. Pegar todas as solicitações de matrícula e transformar em matrícula em espera</li>
 * <li>2. Para cada Discente:
 * 	<ul><li>2.1. Chamar DiscenteCalculosHelper</li></ul></li>
 * </ul>
 * 
 * @author David Pereira
 *
 */
public class ProcessadorPreProcessamentoMatricula extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		ProcessamentoMatriculaDAO dao = getDAO(ProcessamentoMatriculaDAO.class, mov);
		PreProcessamentoMov ppMov = (PreProcessamentoMov) mov;
		
		try {
			
			// Criar matrículas em espera a partir de solicitações
			if (SigaaListaComando.PRE_PROCESSAR_MATRICULA.equals(mov.getCodMovimento())) {
				SolicitacaoMatricula solicitacao = ppMov.getSolicitacao();
				MatriculaComponente matricula = null;
				
				if (ppMov.getModo().equals(ModoProcessamentoMatricula.EAD)) {
					if (!solicitacao.isNegada())
						matricula = solicitacao.criarMatriculaMatriculado();
				} else {
					matricula = solicitacao.criarMatriculaEmEspera();
				}
				
				
				if (matricula != null && !dao.existeMatricula(matricula)) {
					System.out.println("idTurma = " + matricula.getTurma().getId() + "; idDiscente = " + matricula.getDiscente().getId());
					matricula.setRematricula(ppMov.isRematricula());
					dao.create(matricula);
					
					solicitacao.setMatriculaGerada(matricula);
					dao.updateField(SolicitacaoMatricula.class, solicitacao.getId(), "matriculaGerada", matricula.getId());
				}
				
			} else {
				Integer id = ppMov.getDiscente();
				
				// Para cada turma busca as matrículas e ordena
				DiscenteGraduacao dg = dao.findByPrimaryKey(id, DiscenteGraduacao.class);
				//DiscenteCalculosHelper.atualizarTodosCalculosDiscente(dg, mov);
				//DiscenteGraduacao dg2 = dao2.findByPrimaryKey(id, DiscenteGraduacao.class);
				DiscenteCalculosHelper.atualizaDadosPreProcessamento(dg, mov);
				System.out.println("Processado " + dg.getId());
			}
			
		} finally {
			dao.close();
		}

		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
