/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 11/02/2011
 * Autor: Lindemberg Silva
 *
 */

package br.ufrn.integracao.interfaces;

import java.io.Serializable;

import javax.jws.WebService;

import org.springframework.remoting.RemoteAccessException;

import br.ufrn.integracao.dto.AfastamentoDTO;

/**
 * Interface Remota para o SIPAC se comunicar com o SIGRH. 
 * Exposta atrav�s do Spring HTTP Invoker (Spring Remoting).
 * Atrav�s dessa interface remota o SIPAC obt�m informa��es de afastamentos vinculados � solicita��o de informativo.
 * 
 * @author Lindemberg Silva
 *
 */
@WebService
public interface InformativoAfastamentoRemoteService extends Serializable {
	
	/**
	 * Verifica se a solicita��o de informativo no SIPAC est� vinculado a algum afastamento homologado no SIGRH.
	 */
	public boolean existeAfastamentoHomologadoVinculado(int idSolicitacaoInformativo) throws RemoteAccessException;

	/**
	 * Recupera informa��es de uma aus�ncia para popular requisi��es de passagem ou di�rias.
	 */
	public AfastamentoDTO findInformacoesAusencia(int numero, int ano) throws RemoteAccessException;
}
