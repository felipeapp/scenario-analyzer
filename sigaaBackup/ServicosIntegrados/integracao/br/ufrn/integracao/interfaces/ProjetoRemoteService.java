/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
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
 * Interface Remota para comunica��o com os dados do projeto no SIPAC. 
 *
 * @author Eduardo Costa (UFRN)
 */
@WebService
public interface ProjetoRemoteService {

	/**
	 * M�todo respons�vel por rertonar o projeto referente ao n�mero formatado passado como par�metro.
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
	 * M�todo respons�vel por rertonar o projeto referente ao n�mero formatado passado como par�metro com o detalhameto apenas da rubrica informada.
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
