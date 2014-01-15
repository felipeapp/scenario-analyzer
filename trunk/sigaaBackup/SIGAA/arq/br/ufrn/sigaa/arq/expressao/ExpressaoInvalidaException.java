/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/13 - 17:43:33
 */
package br.ufrn.sigaa.arq.expressao;

import br.ufrn.arq.erros.RuntimeNegocioException;

/**
 * Exce��o disparada quando uma express�o de pr�-requisitos, co-requisitos
 * ou equival�ncia est� mal-formada.
 * 
 * @author David Pereira
 *
 */
public class ExpressaoInvalidaException extends RuntimeNegocioException {

	public ExpressaoInvalidaException(String msg) {
		super(msg);
	}
	
}
