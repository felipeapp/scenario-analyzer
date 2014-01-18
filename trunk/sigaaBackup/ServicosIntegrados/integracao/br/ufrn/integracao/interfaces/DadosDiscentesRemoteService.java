/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 25/06/2013
 * Autor: Diego J�come
 */
package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.List;

import javax.jws.WebService;

import br.ufrn.integracao.dto.dados_discentes.PessoaDTO;


/**
 * Interface Remota de discentes para que os sistemas se comuniquem com o SIGAA atrav�s do Spring HTTP Invoker (Spring Remotting).
 * 
 * @author Diego J�come
 *
 */
@WebService
public interface DadosDiscentesRemoteService extends Serializable {

	/**
	 * Retorna um mapeamento entre o id da pessoa e os dados do discente. 
	 * @return
	 */
	public List<PessoaDTO> findDiscentesByPessoas(List <Integer> ids);
}
