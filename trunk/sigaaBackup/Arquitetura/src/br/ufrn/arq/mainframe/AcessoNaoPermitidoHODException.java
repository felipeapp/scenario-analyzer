/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 13/06/2012
 * Autor:     Johnny Marçal
 *
 */
package br.ufrn.arq.mainframe;

import br.ufrn.arq.erros.RuntimeNegocioException;

/**
 * 
 * Indica que o usuário não está credenciado para acessar o sistema estruturante do SERPRO.
 * Neste caso, o usuário deve entrar em contato com o suporte do SERPRO para ter seu acesso permitido.
 * 
 * @author Johnny Marçal
 *
 */
public class AcessoNaoPermitidoHODException extends RuntimeNegocioException {

	public AcessoNaoPermitidoHODException(String msg) {
		super(msg);
	}
}
