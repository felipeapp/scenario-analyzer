/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 04/10/2007
 */
package br.ufrn.arq.erros;

/**
 * Exceção disparada quando uma consulta retorna um número
 * maior de resultados que o permitido, exigindo que o usuário
 * restrinja os termos da busca.
 * 
 * @author Gleydson Lima
 * @author David Pereira
 *
 */
public class LimiteResultadosException extends DAOException {

    public LimiteResultadosException(String msg) {
        super(msg);
        setNotificavel(false);
    }

	public LimiteResultadosException() {
		super("O limite de resultados foi ultrapassado. Por favor, refine a consulta.");
		setNotificavel(false);
	}
    public LimiteResultadosException(Exception e) {
        super("O limite de resultados foi ultrapassado. Por favor, refine a consulta.");
        setNotificavel(false);
    }

    public LimiteResultadosException(String mensagem, Exception e) {
    	super(mensagem, e);
    	setNotificavel(false);
    }

    public LimiteResultadosException(int codErro, String msg) {
        super(codErro, msg);
        setNotificavel(false);
    }
}

