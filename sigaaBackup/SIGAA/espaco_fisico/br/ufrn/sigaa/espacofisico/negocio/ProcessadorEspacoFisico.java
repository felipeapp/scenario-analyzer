/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.espacofisico.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.espacofisico.EspacoFisicoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.espacofisico.dominio.EspacoFisico;
import br.ufrn.sigaa.espacofisico.dominio.RecursoEspacoFisico;

/**
 * Processador para executar as opera��es de neg�cio do espa�o f�sico
 * 
 * @author Henrique Andr�
 *
 */
public class ProcessadorEspacoFisico extends AbstractProcessador {

	/**
	 * M�todo invocado automaticamente, executa o neg�cio
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
	
		validate(mov);
		
		MovimentoEspacoFisico movEsp = (MovimentoEspacoFisico) mov;
		
		if (SigaaListaComando.ADICIONAR_ESPACO_FISICO.equals(movEsp.getCodMovimento()))
			criarEspacoFisico(movEsp);
		else if (SigaaListaComando.ALTERAR_ESPACO_FISICO.equals(movEsp.getCodMovimento()))
			atualizarEspacoFisico(movEsp);
		else if (SigaaListaComando.REMOVER_ESPACO_FISICO.equals(movEsp.getCodMovimento()))
			removerEspacoFisico(movEsp);
		
		return "";
	}

	/**
	 * Remove um espa�o f�sico (Seta ativo para FALSE)
	 * 
	 * @param movEsp
	 * @throws DAOException
	 */
	private void removerEspacoFisico(MovimentoEspacoFisico movEsp) throws DAOException {
		EspacoFisico espacoFisico = movEsp.getEspacoFisico();
		espacoFisico.setAtivo(false);
		
		EspacoFisicoDao dao = getDAO(EspacoFisicoDao.class, movEsp);
		
		dao.update(espacoFisico);
	}

	/**
	 * Cria um novo espa�o f�sico
	 * 
	 * @param movEsp
	 * @throws DAOException
	 */
	private void criarEspacoFisico(MovimentoEspacoFisico movEsp) throws DAOException {
		EspacoFisico espacoFisico = movEsp.getEspacoFisico();
		
		EspacoFisicoDao dao = getDAO(EspacoFisicoDao.class, movEsp);
		
		dao.create(espacoFisico);
	}

	/**
	 * Atualiza o espa�o f�sico
	 * 
	 * @param movEsp
	 * @throws DAOException
	 */
	private void atualizarEspacoFisico(MovimentoEspacoFisico movEsp) throws DAOException {
		EspacoFisicoDao dao = getDAO(EspacoFisicoDao.class, movEsp);
		
		EspacoFisico espacoFisico = movEsp.getEspacoFisico();
		List<RecursoEspacoFisico> recursosRemovidos = movEsp.getRecursosRemovidos();
		
		for (RecursoEspacoFisico recursoRemovido : recursosRemovidos) {
			recursoRemovido.setAtivo(false);
			dao.update(recursoRemovido);
		}
		
		dao.update(espacoFisico);
	}	
	
	/**
	 * Valida a cria��o de um novo espa�o f�sico
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoEspacoFisico movEsp = (MovimentoEspacoFisico) mov;
		EspacoFisico espacoFisico = movEsp.getEspacoFisico();
		
		if (SigaaListaComando.ADICIONAR_ESPACO_FISICO.equals(movEsp.getCodMovimento())) {
			EspacoFisicoValidator.isCodigoDisponivel(espacoFisico.getCodigo());
		}
	}

}
