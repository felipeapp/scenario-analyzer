/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 19/09/2007
 *
 */	
package br.ufrn.sigaa.assistencia.negocio;

import java.rmi.RemoteException;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.sae.DiasAlimentacaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.restaurante.dominio.DiasAlimentacao;

/**
 * Processador para cadastro de Dias de Alimentação
 * e seus relacionamentos com  Bolsa Auxílio
 * 
 * @author David Pereira
 *
 */
public class ProcessadorCadastroDiasAlimentacao extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoDiasAlimentacao pMov = (MovimentoDiasAlimentacao) mov; 
		
		try {
			if (SigaaListaComando.CADASTRAR_DIAS_ALIMENTACAO.equals(mov.getCodMovimento()))
				cadastrar(pMov);
			else if (SigaaListaComando.ATUALIZAR_DIAS_ALIMENTACAO.equals(mov.getCodMovimento()))
				atualizar(pMov);
			else
				remover(pMov);
		} catch(ConstraintViolationException e) {
			throw new NegocioException("Não foi possível remover este pólo pois ele está sendo usado pelo sistema.");
		}
		
		return null;
	}

	/**
	 * Atualiza os dias de alimentação que um discente tem direito
	 * @param pMov
	 * @throws DAOException
	 */
	private void atualizar(MovimentoDiasAlimentacao pMov) throws DAOException {
		DiasAlimentacaoDao dao = getDAO(DiasAlimentacaoDao.class, pMov);
		List<DiasAlimentacao> diasAlimentacaos = pMov.getDiasAlimentacao();
		
		for (DiasAlimentacao dia : diasAlimentacaos) {
			dao.update(dia);
		}
	}

	/**
	 * Cadastra os dias de alimentação que um discente tem direito
	 * @param mov
	 * @throws DAOException
	 */
	private void cadastrar(MovimentoDiasAlimentacao mov) throws DAOException {
		DiasAlimentacaoDao dao = getDAO(DiasAlimentacaoDao.class, mov);
		List<DiasAlimentacao> diasAlimentacaos = mov.getDiasAlimentacao();
		for (DiasAlimentacao dia : diasAlimentacaos) {
			dao.create(dia);
		}
	}
	
	private void remover(MovimentoDiasAlimentacao mov) throws DAOException {
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
