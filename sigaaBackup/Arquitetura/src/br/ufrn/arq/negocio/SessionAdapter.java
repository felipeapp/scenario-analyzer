/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 03/09/2004
 */
package br.ufrn.arq.negocio;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

/**
 * Adaptador de EJB, usado para tirar lógica de rede.
 *
 * @author Gleydson Lima
 */
public class SessionAdapter implements SessionBean {

	protected SessionContext sc; 
	
	public void ejbCreate() throws CreateException {
	    
	}
	
	public void ejbActivate() throws EJBException, RemoteException {
	}

	public void ejbPassivate() throws EJBException, RemoteException {
	}

	public void ejbRemove() throws EJBException, RemoteException {
	}

	public void setSessionContext(SessionContext sc) throws EJBException, RemoteException {
		this.sc = sc;
	}

}
