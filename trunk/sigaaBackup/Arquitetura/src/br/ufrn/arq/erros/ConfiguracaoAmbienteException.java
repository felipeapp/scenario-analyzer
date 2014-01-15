/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 29/04/2010
 */
package br.ufrn.arq.erros;


/**
 * Esta exceção é emitida sempre que o erro de configuração no ambiente.
 * Por exemplo: falta de parametros.
 *
 * @author Gleydson Lima
 * @author David Pereira
 */
public class ConfiguracaoAmbienteException extends RuntimeException {

	public ConfiguracaoAmbienteException(Exception e) {
		super(e);
	}

	public ConfiguracaoAmbienteException(String msg) {
		super(msg);
	}
	
	public ConfiguracaoAmbienteException(String msg, Exception e) {
		super(msg, e);
	}

	public ConfiguracaoAmbienteException() {
		super("Erro de configuração no ambiente");
	}

}