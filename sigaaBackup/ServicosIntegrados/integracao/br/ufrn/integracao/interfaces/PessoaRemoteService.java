/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 27/01/2011
 */

package br.ufrn.integracao.interfaces;

import java.io.Serializable;

import javax.jws.WebService;

import br.ufrn.integracao.exceptions.NegocioRemotoException;

/**
 * Interface respons�vel pela comunica��o remota atrav�s de Spring Remote 
 * para realizar opera��es sobre a Pessoa do Sipac
 * 
 * @author M�rio Melo
 *
 */
@WebService
public interface PessoaRemoteService extends Serializable {
	
	public void atualizarContaPessoa(int idPessoa,int idBanco,String conta, String agencia) throws NegocioRemotoException;
}
