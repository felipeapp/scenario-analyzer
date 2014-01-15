/**
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 25/06/2012
 * Autor:     Johnny Mar�al
 *
 */
package br.ufrn.arq.mainframe;

import br.ufrn.arq.erros.RuntimeNegocioException;

/**
 * @author Johnny Mar�al
 *
 */
public class ErroProcessamentoMainFrameException extends
		RuntimeNegocioException {

	public ErroProcessamentoMainFrameException(String msg) {
		super(msg);
	}	
}
