/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 02/09/2004
 */
package br.ufrn.arq.negocio;

import java.rmi.RemoteException;

import javax.ejb.EJBObject;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;

/**
 * Interface Remota para o processador de Comandos
 * 
 * @author Gleydson Lima
 */
public interface Processador extends EJBObject {

    public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException;
    
}