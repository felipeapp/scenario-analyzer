/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 25/06/2012
 * Autor:     Johnny Marçal
 *
 */
package br.ufrn.arq.mainframe;

import br.ufrn.arq.erros.RuntimeNegocioException;

/**
 * @author Johnny Marçal
 *
 */
public class ErroProcessamentoMainFrameException extends
		RuntimeNegocioException {

	public ErroProcessamentoMainFrameException(String msg) {
		super(msg);
	}	
}
