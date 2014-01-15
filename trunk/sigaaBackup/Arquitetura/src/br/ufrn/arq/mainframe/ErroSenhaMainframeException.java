/**
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 18/07/2012
 * Autor:     Johnny Mar�al
 *
 */
package br.ufrn.arq.mainframe;

import br.ufrn.arq.erros.RuntimeNegocioException;

/**
 * 
 * Indica que ocorreu um erro de senha no acesso ao sistema estruturante do SERPRO.
 * Tr�s erros sucessivos revogam o acesso do usu�rio ao sistema do SERPRO.
 * 
 * @author Johnny Mar�al
 *
 */
public class ErroSenhaMainframeException extends RuntimeNegocioException {

	public ErroSenhaMainframeException(String msg) {
		super(msg);
	}	
}
