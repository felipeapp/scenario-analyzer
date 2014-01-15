/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 01/01/2009
 *
 */	
package br.ufrn.integracao.exceptions;

/**
 * Exceção disparada quando uma pessoa não é identificada pelo leitor
 * da digital. Pode ser disparada quando a aplicação desktop comunica-se
 * com o servidor.
 * 
 * @author agostinho campos
 */
public class PessoaNaoIdentificadaDigitalExcpetion extends Exception {

        public PessoaNaoIdentificadaDigitalExcpetion(String mensagem) {
            super(mensagem);
        }
}
