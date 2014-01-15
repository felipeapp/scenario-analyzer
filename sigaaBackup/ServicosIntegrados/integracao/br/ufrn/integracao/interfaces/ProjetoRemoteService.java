/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/04/2012
 */
package br.ufrn.integracao.interfaces;

import java.security.PublicKey;

import javax.jws.WebService;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.integracao.exceptions.NegocioRemotoException;
import fundacao.integracao.projetos.ProjetoDTO;

/**
 * Interface Remota para comunicação com os dados do projeto no SIPAC. 
 *
 * @author Eduardo Costa (UFRN)
 */
@WebService
public interface ProjetoRemoteService {

	/**
	 * Método responsável por rertonar o projeto referente ao número formatado passado como parâmetro.
	 * 
	 * @param numeroFormatado
	 * @param assinaturaDigital
	 * @param chavePublica
	 * @return
	 * @throws DAOException
	 * @throws NegocioRemotoException
	 */
	public ProjetoDTO findProjetoByNumeroFormatado(String numeroFormatado, byte[] assinaturaDigital, PublicKey chavePublica) throws DAOException, NegocioRemotoException;

	/**
	 * Método responsável por rertonar o projeto referente ao número formatado passado como parâmetro com o detalhameto apenas da rubrica informada.
	 * 
	 * @param numeroFormatado
	 * @param rubrica
	 * @param assinaturaDigital
	 * @param chavePublica
	 * @return
	 * @throws DAOException
	 * @throws NegocioRemotoException
	 */
	public ProjetoDTO findProjetoByNumeroFormatadoAndRubrica(String numeroFormatado, long rubrica, byte[] assinaturaDigital, PublicKey chavePublica) throws DAOException, NegocioRemotoException;
	
}
