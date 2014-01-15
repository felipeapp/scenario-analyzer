/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '10/08/2010'
 *
 */

package br.ufrn.integracao.interfaces;

import javax.jws.WebService;

import br.ufrn.arq.erros.DAOException;

/**
 * Interface que define os servi�os a serem expostos referentes aos Memorandos Eletr�nicos.
 * 
 * @author Weinberg Souza
 */
@WebService
public interface MemorandoEletronicoRemoteService {

	
	/**
	 * M�todo utilizado para verificar se o usu�rio possui Memorandos para leitura.
	 * 
	 * @param idUsuario
	 * @return
	 * @throws DAOException
	 */
	public boolean possuiMemorandoEletronico(int idUsuario);
	
	
}
