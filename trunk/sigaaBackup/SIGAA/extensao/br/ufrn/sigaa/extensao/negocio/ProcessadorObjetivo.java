package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Map;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dao.ObjetivoDao;
import br.ufrn.sigaa.extensao.dominio.Objetivo;

public class ProcessadorObjetivo extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		MovimentoCadastro movCad = (MovimentoCadastro) mov;
		movCad.getObjMovimentado();

		if (mov.getCodMovimento().equals(SigaaListaComando.SUBMETER_OBJETIVO)) {
			submeter(movCad);
		}
		if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_OBJETIVO)) {
			remover(movCad);
		}
		if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_CH_OBJETIVO)) {
			atualizarCHMembroProjeto(movCad);
		}
		
		return movCad;
	}

	private void atualizarCHMembroProjeto(MovimentoCadastro movCad) throws DAOException {
		ObjetivoDao dao = getDAO(ObjetivoDao.class, movCad);
		Objetivo objetivo = movCad.getObjMovimentado();
		try {
			Map<Integer, Integer> atualizar = dao.carregarMembrosObjetivo(objetivo.getAtividadeExtensao());
			dao.alterarCHMembroProjeto(atualizar);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
	}

	private void remover(MovimentoCadastro movCad) throws DAOException {
		ObjetivoDao dao = getDAO(ObjetivoDao.class, movCad);
		Objetivo objetivo = movCad.getObjMovimentado();
		try {
			dao.inativarObjetivo(objetivo);
		} finally {
			dao.close();
		}
	}

	private void submeter(MovimentoCadastro movCad) throws DAOException {
		GenericDAO dao = getGenericDAO(movCad);
		Objetivo objetivo = movCad.getObjMovimentado();
		try {
			dao.createOrUpdate(objetivo);
		} finally {
			dao.close();
		}
	}

	
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}