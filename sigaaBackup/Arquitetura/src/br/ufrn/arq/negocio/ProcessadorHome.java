/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 02/09/2004
 */
package br.ufrn.arq.negocio;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

/**
 * Interface Home para todos os processadores
 *
 * @author Gleydson Lima
 */
public interface ProcessadorHome extends EJBHome {

	public Processador create() throws CreateException, RemoteException;
	
}
