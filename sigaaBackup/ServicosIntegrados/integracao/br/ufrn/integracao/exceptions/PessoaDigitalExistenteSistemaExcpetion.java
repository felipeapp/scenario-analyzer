package br.ufrn.integracao.exceptions;

/**
 * Exce��o disparada quando usu�rio j� possui digital cadastrada no sistema. Utilizada
 * no aplicativo de Cadastro de Pessoas (cadastramento de digitais).  
 * @author Agostinho
 */
public class PessoaDigitalExistenteSistemaExcpetion extends Exception {

        public PessoaDigitalExistenteSistemaExcpetion(String mensagem) {
            super(mensagem);
        }
}
