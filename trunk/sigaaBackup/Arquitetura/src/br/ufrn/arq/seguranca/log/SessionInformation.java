/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 28/06/2010
 */
package br.ufrn.arq.seguranca.log;

import org.hibernate.Session;

/**
 * Classe com informações de sessões que foram abertas
 * em um managed bean, action ou procesador e que não
 * foram fechadas.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class SessionInformation {

	private Session session;
	
	private String stackTraceAbertura;
	
	private long tempoAbertura;

	public SessionInformation() {
		
	}
	
	public SessionInformation(Session session, String stackTraceAbertura) {
		this.session = session;
		this.stackTraceAbertura = stackTraceAbertura;
		this.tempoAbertura = System.currentTimeMillis();
	}
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getStackTraceAbertura() {
		return stackTraceAbertura;
	}

	public void setStackTraceAbertura(String stackTraceAbertura) {
		this.stackTraceAbertura = stackTraceAbertura;
	}

	public long getTempoAbertura() {
		return tempoAbertura;
	}

	public void setTempoAbertura(long tempoAbertura) {
		this.tempoAbertura = tempoAbertura;
	}
	
}
