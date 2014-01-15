/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '10/08/2010'
 *
 */

package br.ufrn.integracao.interfaces;

import javax.jws.WebService;

import br.ufrn.arq.erros.DAOException;

/**
 * Interface que define os serviços a serem expostos referentes aos Memorandos Eletrônicos.
 * 
 * @author Weinberg Souza
 */
@WebService
public interface MemorandoEletronicoRemoteService {

	
	/**
	 * Método utilizado para verificar se o usuário possui Memorandos para leitura.
	 * 
	 * @param idUsuario
	 * @return
	 * @throws DAOException
	 */
	public boolean possuiMemorandoEletronico(int idUsuario);
	
	
}
