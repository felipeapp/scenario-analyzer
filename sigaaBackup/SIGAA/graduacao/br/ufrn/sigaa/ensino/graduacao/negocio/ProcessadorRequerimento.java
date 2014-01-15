/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.graduacao.RequerimentoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.graduacao.dominio.Requerimento;

/**
 * @author Henrique Andre
 */
public class ProcessadorRequerimento extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		MovimentoRequerimento movReq = (MovimentoRequerimento) mov;

		if (movReq.getCodMovimento() == SigaaListaComando.GRAVAR_REQUERIMENTO)
			gravarRequerimento(movReq);
		else if (movReq.getCodMovimento() == SigaaListaComando.ENVIAR_REQUERIMENTO)
			enviarRequerimento(movReq);

		return null;
	}

	/*
	 * Enviar o requerimento, aluno não pode mais editar	
	 */
	private void enviarRequerimento(MovimentoRequerimento mov)
			throws DAOException {

		gravarRequerimento(mov);
	}
	/*
	 * Apenas grava, o aluno ainda pode alterar 
	 */
	private void gravarRequerimento(MovimentoRequerimento mov)
			throws DAOException {
		
		RequerimentoDao dao = getDAO(RequerimentoDao.class, mov);
		
		Requerimento requerimento = mov.getRequerimento();
		if (requerimento.getId() <= 0) {
			int ano = Calendar.getInstance().get(Calendar.YEAR);
			
			int sequence = dao.getNextSeq("graduacao.requerimento_" + ano);
			requerimento.setCodigoProcesso(sequence);
			
			if (requerimento.getDataAtualizado() == null)
				requerimento.setDataAtualizado(new Date());
			
			dao.create(requerimento);
		} else {
			dao.detach(requerimento);
			dao.update(requerimento);
		}
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
