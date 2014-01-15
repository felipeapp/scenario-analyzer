/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 21/05/2012
 * Autor:     Johnny Marçal
 *
 */
package br.ufrn.arq.mainframe;

import br.ufrn.arq.erros.RuntimeNegocioException;

/**
 * @author johnnycms
 *
 */
public class UsuarioRevogadoHODException extends RuntimeNegocioException {

	public UsuarioRevogadoHODException(String msg) {
		super(msg);
	}
	
}
