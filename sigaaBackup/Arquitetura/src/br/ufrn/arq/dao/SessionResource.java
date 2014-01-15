/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 14/06/2010
 */
package br.ufrn.arq.dao;

import org.hibernate.Session;

/**
 * Adapter de uma Session do Hibernate para a interface
 * ClosableResource. Permite que as sessões sejam fechadas
 * após a execução de um processador. 
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class SessionResource implements ClosableResource {

	private Session session;
	
	public SessionResource(Session session) {
		this.session = session;
	}
	
	@Override
	public void close() {
		if (session.isOpen())
			session.close();
	}

}
