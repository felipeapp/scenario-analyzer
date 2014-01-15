/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 14/09/2011
 */
package br.ufrn.arq.dao;

import java.sql.Connection;
import java.util.Map;

import org.hibernate.Session;

/**
 * Classe para armazenar recursos do sistema (conexões e sessions
 * do Hibernate) em escopo thread para posterior fechamento.
 * 
 * @author David Pereira
 *
 */
public final class ThreadScopedResourceCache {

	private ThreadScopedResourceCache() { }
	
	/** Session utilizada durante a thread */
	public static ThreadLocal<Map<Integer, Session>> sessionsCache = new ThreadLocal<Map<Integer, Session>>();

	/** Conexões por sistema utilizadas durante a thread */
	public static ThreadLocal<Map<Integer, Connection>> connectionsCache = new ThreadLocal<Map<Integer, Connection>>();

	/**
	 * Fecha todos os recursos que foram abertos pela thread
	 */
	public static synchronized void closeResources() {
		Map<Integer, Session> sessions = sessionsCache.get();
		if (sessions != null) {
			for (Session session : sessions.values()) {
				if (session.isOpen()) session.close();
			}
			
			sessionsCache.remove();
		}
		
		Map<Integer, Connection> connections = connectionsCache.get();
		if (connections != null) {
			for (Connection con : connections.values()) {
				Database.getInstance().close(con);
			}
			connectionsCache.remove();
		}
	}
	
}
