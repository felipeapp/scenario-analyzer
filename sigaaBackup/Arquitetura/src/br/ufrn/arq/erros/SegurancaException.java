/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 25/10/2004
 */
package br.ufrn.arq.erros;


/**
 * Exceção disparada para falhas de segurança
 *
 * @author Gleydson Lima
 *
 */
public class SegurancaException extends ArqException {

	private int[] papeis;
	
	public SegurancaException(String msg) {
        super(msg);
        setNotificavel(false);
    }
	
    public SegurancaException(String msg, int[] papeis) {
        super(msg);
        setNotificavel(false);
        this.papeis = papeis;
    }

    public SegurancaException() {
        super("Usuário não autorizado a realizar esta operação");
        setNotificavel(false);
    }

    public SegurancaException(Exception e) {
        super(e);
        setNotificavel(false);
    }

	public int[] getPapeis() {
		return papeis;
	}

}
