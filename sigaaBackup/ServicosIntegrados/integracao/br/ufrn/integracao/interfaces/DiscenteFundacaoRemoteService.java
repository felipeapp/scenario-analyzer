/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 04/12/2012
 * Autor: Rafael Gomes
 */
package br.ufrn.integracao.interfaces;

import java.io.Serializable;
import java.util.List;

import javax.jws.WebService;

import fundacao.integracao.academico.DiscenteDTO;

/**
 * Interface Remota de discentes para que os sistemas se comuniquem com o SIGAA atrav�s do Spring HTTP Invoker (Spring Remotting).
 * 
 * @author Rafael Gomes
 *
 */
@WebService
public interface DiscenteFundacaoRemoteService extends Serializable{
	
	/**
	 * Retorna a lista de todos os discentes para servi�o remoto com o sistema da Funda��o. 
	 * @return
	 */
	public List<DiscenteDTO> findAllDiscenteFundacao();
}
