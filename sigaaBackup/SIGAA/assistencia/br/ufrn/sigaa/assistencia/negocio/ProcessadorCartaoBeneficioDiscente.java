/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 23/02/2011
 *
 */	
package br.ufrn.sigaa.assistencia.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.sae.CartaoBeneficioDiscenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.dominio.CartaoBeneficioDiscente;
import br.ufrn.sigaa.assistencia.dominio.CartaoBolsaAlimentacao;

/**
 * Processador respons�vel por opera��es envolvendo cart�es de acesso ao ru
 * 
 * @author geyson
 *
 */
public class ProcessadorCartaoBeneficioDiscente extends AbstractProcessador {

	/** executa opera��es que envolve cart�es de benef�cio de discentes  */ 
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		CartaoBeneficioDiscente cartao = mov.getObjMovimentado();
	
		if (mov.getCodMovimento().equals(SigaaListaComando.ASSOCIAR_PESSOA_CARTAO_RU)) {
			associarDiscente(cartao, mov);
		}

		if (mov.getCodMovimento().equals(SigaaListaComando.BLOQUEAR_CARTAO_BENEFICIO)) {
			bloquearCartaoDiscente(cartao, mov);
		}
				
		return cartao;
	}

	/**
	 * Associa discente a um cart�o alimenta��o. 
	 * @param cartao
	 * @param mov
	 * @throws DAOException
	 */
	private void associarDiscente(CartaoBeneficioDiscente cartao, Movimento mov) throws DAOException {
			CartaoBeneficioDiscenteDao dao = getDAO(CartaoBeneficioDiscenteDao.class, mov);
			try{
				Collection<CartaoBeneficioDiscente> cartoes = new ArrayList<CartaoBeneficioDiscente>();
	            cartoes = dao.cartoesBeneficioDiscente(cartao.getCartaoBolsaAlimentacao().getId());
	            
	            if(!cartoes.isEmpty()){
		            	dao.inativarCartoes(cartoes.iterator().next().getCartaoBolsaAlimentacao());
	            }
				cartao.getCartaoBolsaAlimentacao().setBloqueado(false);
				getGenericDAO(mov).updateField(CartaoBolsaAlimentacao.class, cartao.getCartaoBolsaAlimentacao().getId(), "bloqueado", false);
				getGenericDAO(mov).createOrUpdate(cartao);
			}finally{
				dao.close();
			}
	}
	
	/**
	 * Opera��o para bloquear cart�o.
	 * @param cartao
	 * @param mov
	 * @throws DAOException
	 */
	private void bloquearCartaoDiscente(CartaoBeneficioDiscente cartao, Movimento mov) throws DAOException {
			cartao.getCartaoBolsaAlimentacao().setBloqueado(true);
			cartao.setAtivo(false);
			getGenericDAO(mov).createOrUpdate(cartao.getCartaoBolsaAlimentacao());
			getGenericDAO(mov).createOrUpdate(cartao);
	}

	/** valida��es */
	@Override
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		
	}

}
