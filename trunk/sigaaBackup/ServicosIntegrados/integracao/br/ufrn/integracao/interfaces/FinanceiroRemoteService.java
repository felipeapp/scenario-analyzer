package br.ufrn.integracao.interfaces;

import java.io.Serializable;

import javax.jws.WebService;

import br.ufrn.arq.erros.DAOException;

/**
 * Interface Remota para comunica��o entre SIPAC e SIGRH para consultar dados financeiros de um determinado servidor.
 * 
 * @author Marcelo Maia
 */

@WebService
public interface FinanceiroRemoteService extends Serializable {
	/**
	 * Verifica se o servidor passado como par�metro recebe algum tipo de aux�lio alimenta��o.
	 * 
	 * @param idServidor
	 * @param idsRubrica
	 * @return
	 * @throws DAOException
	 */
	public boolean existeValeAlimentacao(int idServidor, String[] idsRubrica) throws Exception ;
}
