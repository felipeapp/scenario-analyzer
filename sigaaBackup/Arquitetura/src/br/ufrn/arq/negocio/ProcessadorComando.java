/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/09/2004
 */
package br.ufrn.arq.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;

/**
 * Interface que todo processador de comando deve ter.
 * 
 * @author Gleydson Lima
 *  
 */
public interface ProcessadorComando {

    public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException;

    public void validate(Movimento mov) throws NegocioException, ArqException;

}