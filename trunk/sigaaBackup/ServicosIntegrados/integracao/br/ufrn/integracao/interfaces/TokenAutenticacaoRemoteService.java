/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/04/2010
 */
package br.ufrn.integracao.interfaces;

import br.ufrn.arq.seguranca.autenticacao.TokenGenerator;


/**
 * Interface para serviço remoto de geração de token de autenticação.
 * Utilizado para diminuir a dependência entre a classe que estiver usando
 * e o serviço de token, especialmente em sistemas desktop. 
 * 
 * @author David Pereira
 *
 */
public interface TokenAutenticacaoRemoteService {

	/**
	 * Cria e registra um token no banco de dados utilizando os métodos
	 * generateToken e registerToken de {@link TokenGenerator}. 
	 * @see TokenGenerator
	 * @param idRegistroEntrada
	 * @param sistema
	 * @param info
	 * @return
	 */
	public Object[] registerToken(Integer idRegistroEntrada, int sistema, String... info);

}
