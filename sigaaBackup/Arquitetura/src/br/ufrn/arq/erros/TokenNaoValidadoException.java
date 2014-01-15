/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/04/2010
 */
package br.ufrn.arq.erros;

import br.ufrn.arq.seguranca.dominio.TokenAutenticacao;

/**
 * Exceção para identificar quanto um {@link TokenAutenticacao}
 * não foi validado, portanto a autententicação não
 * foi bem sucedida e a ação não deve ser realizada.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class TokenNaoValidadoException extends RuntimeException {

	public TokenNaoValidadoException(String msg) {
        super(msg);
    }

    public TokenNaoValidadoException(Throwable cause) {
        super(cause);
    }

    public TokenNaoValidadoException(String msg, Throwable cause) {
    	super(msg, cause);
    }
	
}
