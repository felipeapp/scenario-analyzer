/**
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 09/05/2012
 * Autor:     Johnny Mar�al
 *
 */
package br.ufrn.arq.mainframe;

import br.ufrn.arq.erros.RuntimeNegocioException;

/**
 * @author johnnycms
 *
 */
public class MainFrameConnectionException extends RuntimeNegocioException {

	public MainFrameConnectionException(String msg) {
		super(msg);
	}
}
