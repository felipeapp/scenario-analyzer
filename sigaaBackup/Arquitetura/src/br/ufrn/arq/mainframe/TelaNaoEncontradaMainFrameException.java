/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 28/05/2012
 * Autor:     Johnny Marçal
 *
 */
package br.ufrn.arq.mainframe;

import br.ufrn.arq.erros.RuntimeNegocioException;

/**
 * 
 * Indica que uma informação que deveria estar presente na tela do sistema estruturante do SERPRO
 * não foi encontrada.
 * Pode indicar que a tela teve seu layout alterado.
 * 
 * @author Johnny Marçal
 *
 */
public class TelaNaoEncontradaMainFrameException extends RuntimeNegocioException {

	public TelaNaoEncontradaMainFrameException(String msg) {
		super(msg);
	}	
}
