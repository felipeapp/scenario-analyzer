/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Processador para executar as operações de negócio do espaço físico
 * 
 * @author Henrique André
 *
 */
public class ProcessadorEspacoFisico extends AbstractProcessador {

	/**
	 * Método invocado automaticamente, executa o negócio
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
	 * Remove um espaço físico (Seta ativo para FALSE)
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
	 * Cria um novo espaço físico
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
	 * Atualiza o espaço físico
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
	 * Valida a criação de um novo espaço físico
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoEspacoFisico movEsp = (MovimentoEspacoFisico) mov;
		EspacoFisico espacoFisico = movEsp.getEspacoFisico();
		
		if (SigaaListaComando.ADICIONAR_ESPACO_FISICO.equals(movEsp.getCodMovimento())) {
			EspacoFisicoValidator.isCodigoDisponivel(espacoFisico.getCodigo());
		}
	}

}
