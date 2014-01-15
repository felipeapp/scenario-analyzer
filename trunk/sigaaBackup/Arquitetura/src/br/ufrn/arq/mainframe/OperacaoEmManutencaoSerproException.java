/**
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 14/06/2012
 * Autor:     Johnny Mar�al
 *
 */
package br.ufrn.arq.mainframe;

import br.ufrn.arq.erros.RuntimeNegocioException;

/**
 * @author johnnycms
 *
 */
public class OperacaoEmManutencaoSerproException extends RuntimeNegocioException{

	public OperacaoEmManutencaoSerproException(String msg) {
		super(msg);
	}
}
