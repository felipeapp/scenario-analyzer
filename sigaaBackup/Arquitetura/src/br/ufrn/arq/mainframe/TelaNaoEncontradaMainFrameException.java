/**
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 28/05/2012
 * Autor:     Johnny Mar�al
 *
 */
package br.ufrn.arq.mainframe;

import br.ufrn.arq.erros.RuntimeNegocioException;

/**
 * 
 * Indica que uma informa��o que deveria estar presente na tela do sistema estruturante do SERPRO
 * n�o foi encontrada.
 * Pode indicar que a tela teve seu layout alterado.
 * 
 * @author Johnny Mar�al
 *
 */
public class TelaNaoEncontradaMainFrameException extends RuntimeNegocioException {

	public TelaNaoEncontradaMainFrameException(String msg) {
		super(msg);
	}	
}
