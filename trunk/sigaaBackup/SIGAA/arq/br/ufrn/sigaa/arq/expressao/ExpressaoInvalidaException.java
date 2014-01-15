/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/13 - 17:43:33
 */
package br.ufrn.sigaa.arq.expressao;

import br.ufrn.arq.erros.RuntimeNegocioException;

/**
 * Exceção disparada quando uma expressão de pré-requisitos, co-requisitos
 * ou equivalência está mal-formada.
 * 
 * @author David Pereira
 *
 */
public class ExpressaoInvalidaException extends RuntimeNegocioException {

	public ExpressaoInvalidaException(String msg) {
		super(msg);
	}
	
}
