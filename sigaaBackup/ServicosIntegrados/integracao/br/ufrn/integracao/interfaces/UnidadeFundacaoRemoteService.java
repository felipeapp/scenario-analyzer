/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 30/10/2012.
 */
package br.ufrn.integracao.interfaces;

import java.rmi.RemoteException;
import java.util.List;

import javax.jws.WebService;

import fundacao.integracao.comum.UnidadeDTO;

/**
 * Interface do web service para disponibiliza��o das unidades para o sistema da funda��o.
 * em um sistema.
 *
 * @author Itamir Filho
 *
 */
@WebService
public interface UnidadeFundacaoRemoteService {

	/**
	 * M�todo que lista todas as unidades cadastradas.
	 * @return
	 */
	public List<UnidadeDTO> listarUnidades();
	
	/**
	 * M�todo para buscar uma unidade por chave prim�ria.
	 * @param idUnidade
	 * @return
	 * @throws RemoteException 
	 */
	public UnidadeDTO buscarUnidade(int idUnidade);
}

