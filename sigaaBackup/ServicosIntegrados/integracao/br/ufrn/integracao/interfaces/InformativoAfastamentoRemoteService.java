/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Exposta através do Spring HTTP Invoker (Spring Remoting).
 * Através dessa interface remota o SIPAC obtém informações de afastamentos vinculados à solicitação de informativo.
 * 
 * @author Lindemberg Silva
 *
 */
@WebService
public interface InformativoAfastamentoRemoteService extends Serializable {
	
	/**
	 * Verifica se a solicitação de informativo no SIPAC está vinculado a algum afastamento homologado no SIGRH.
	 */
	public boolean existeAfastamentoHomologadoVinculado(int idSolicitacaoInformativo) throws RemoteAccessException;

	/**
	 * Recupera informações de uma ausência para popular requisições de passagem ou diárias.
	 */
	public AfastamentoDTO findInformacoesAusencia(int numero, int ano) throws RemoteAccessException;
}
