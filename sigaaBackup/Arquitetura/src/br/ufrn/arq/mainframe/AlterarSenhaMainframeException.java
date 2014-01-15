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
 * Indica que o sistema estruturante do SERPRO está solicitando a alteração da senha. 
 * Esse procedimento é solicitado periodicamente e o usuário deve acessar o sistema
 * estruturante diretamente para efetuar a alteração. 
 * 
 * @author Johnny Marçal
 *
 */
public class AlterarSenhaMainframeException extends RuntimeNegocioException {

	public AlterarSenhaMainframeException(String msg) {
		super(msg);
	}	
	
}
