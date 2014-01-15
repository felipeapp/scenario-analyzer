/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/04/2010
 */
package br.ufrn.integracao.interfaces;

import br.ufrn.arq.seguranca.autenticacao.TokenGenerator;


/**
 * Interface para servi�o remoto de gera��o de token de autentica��o.
 * Utilizado para diminuir a depend�ncia entre a classe que estiver usando
 * e o servi�o de token, especialmente em sistemas desktop. 
 * 
 * @author David Pereira
 *
 */
public interface TokenAutenticacaoRemoteService {

	/**
	 * Cria e registra um token no banco de dados utilizando os m�todos
	 * generateToken e registerToken de {@link TokenGenerator}. 
	 * @see TokenGenerator
	 * @param idRegistroEntrada
	 * @param sistema
	 * @param info
	 * @return
	 */
	public Object[] registerToken(Integer idRegistroEntrada, int sistema, String... info);

}
