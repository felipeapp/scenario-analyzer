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
 * Indica que o sistema estruturante do SERPRO est� solicitando a altera��o da senha. 
 * Esse procedimento � solicitado periodicamente e o usu�rio deve acessar o sistema
 * estruturante diretamente para efetuar a altera��o. 
 * 
 * @author Johnny Mar�al
 *
 */
public class AlterarSenhaMainframeException extends RuntimeNegocioException {

	public AlterarSenhaMainframeException(String msg) {
		super(msg);
	}	
	
}
