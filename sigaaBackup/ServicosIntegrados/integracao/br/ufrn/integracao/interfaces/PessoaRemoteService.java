/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 27/01/2011
 */

package br.ufrn.integracao.interfaces;

import java.io.Serializable;

import javax.jws.WebService;

import br.ufrn.integracao.exceptions.NegocioRemotoException;

/**
 * Interface responsável pela comunicação remota através de Spring Remote 
 * para realizar operações sobre a Pessoa do Sipac
 * 
 * @author Mário Melo
 *
 */
@WebService
public interface PessoaRemoteService extends Serializable {
	
	public void atualizarContaPessoa(int idPessoa,int idBanco,String conta, String agencia) throws NegocioRemotoException;
}
