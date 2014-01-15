/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 18/07/2012
 * Autor:     Johnny Marçal
 *
 */
package br.ufrn.arq.mainframe;

import br.ufrn.arq.erros.RuntimeNegocioException;

/**
 * 
 * Indica que ocorreu um erro de senha no acesso ao sistema estruturante do SERPRO.
 * Três erros sucessivos revogam o acesso do usuário ao sistema do SERPRO.
 * 
 * @author Johnny Marçal
 *
 */
public class ErroSenhaMainframeException extends RuntimeNegocioException {

	public ErroSenhaMainframeException(String msg) {
		super(msg);
	}	
}
