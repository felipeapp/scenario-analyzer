/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 12/01/2009 
 *
 */

package br.ufrn.sigaa.processamento.batch;

import java.rmi.RemoteException;

import javax.naming.InitialContext;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.Processador;
import br.ufrn.arq.negocio.ProcessadorHome;

/**
 * Thread mãe das threads do processamento de matrícula. 
 * 
 * @author David Pereira
 * 
 */
public abstract class ProcessamentoBatchThread<T> extends Thread {

	protected ListaProcessamentoBatch<T> lista;
	
	@Override
	public void run() {

		try {
			InitialContext ic = new InitialContext();
			ProcessadorHome home = (ProcessadorHome) ic.lookup("ejb/SigaaFacade");
			Processador remote = home.create();

			while (lista.possuiElementos()) {
				T elemento = lista.getProximoElemento();
				processar(remote, elemento);
				lista.registraProcessado();

				System.out.println(lista.processados);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public abstract void processar(Processador processador, T elemento) throws NegocioException, ArqException, RemoteException;
	
}
