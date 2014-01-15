/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 29/10/2012
 *
 */
package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.List;

import javax.jws.WebService;

import fundacao.integracao.servidores.ServidorDTO;

/**
 * Interface Remota para que os sistemas se comuniquem com o SIGPRH através do Spring HTTP Invoker (Spring Remotting).
 * @author Tiago Hiroshi
 */
@WebService
public interface ServidorFundacaoRemoteService extends Serializable{

	/**
	 * Retorna a lista de todos os servidores para importação com o sistema da Fundação. 
	 * @return
	 */
	public List<ServidorDTO> findAllServidoresFundacao();
	

}
