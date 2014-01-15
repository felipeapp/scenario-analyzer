/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
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
 * Interface do web service para disponibilização das unidades para o sistema da fundação.
 * em um sistema.
 *
 * @author Itamir Filho
 *
 */
@WebService
public interface UnidadeFundacaoRemoteService {

	/**
	 * Método que lista todas as unidades cadastradas.
	 * @return
	 */
	public List<UnidadeDTO> listarUnidades();
	
	/**
	 * Método para buscar uma unidade por chave primária.
	 * @param idUnidade
	 * @return
	 * @throws RemoteException 
	 */
	public UnidadeDTO buscarUnidade(int idUnidade);
}

