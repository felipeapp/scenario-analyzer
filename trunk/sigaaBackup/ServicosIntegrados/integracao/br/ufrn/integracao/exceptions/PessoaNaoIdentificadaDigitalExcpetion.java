/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 01/01/2009
 *
 */	
package br.ufrn.integracao.exceptions;

/**
 * Exce��o disparada quando uma pessoa n�o � identificada pelo leitor
 * da digital. Pode ser disparada quando a aplica��o desktop comunica-se
 * com o servidor.
 * 
 * @author agostinho campos
 */
public class PessoaNaoIdentificadaDigitalExcpetion extends Exception {

        public PessoaNaoIdentificadaDigitalExcpetion(String mensagem) {
            super(mensagem);
        }
}
