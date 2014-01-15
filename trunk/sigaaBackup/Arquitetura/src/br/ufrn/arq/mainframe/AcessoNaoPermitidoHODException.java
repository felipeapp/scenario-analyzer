/**
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 13/06/2012
 * Autor:     Johnny Mar�al
 *
 */
package br.ufrn.arq.mainframe;

import br.ufrn.arq.erros.RuntimeNegocioException;

/**
 * 
 * Indica que o usu�rio n�o est� credenciado para acessar o sistema estruturante do SERPRO.
 * Neste caso, o usu�rio deve entrar em contato com o suporte do SERPRO para ter seu acesso permitido.
 * 
 * @author Johnny Mar�al
 *
 */
public class AcessoNaoPermitidoHODException extends RuntimeNegocioException {

	public AcessoNaoPermitidoHODException(String msg) {
		super(msg);
	}
}
