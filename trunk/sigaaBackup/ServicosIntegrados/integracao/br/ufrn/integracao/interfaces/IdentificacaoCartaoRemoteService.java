/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 16/02/2011
 */
package br.ufrn.integracao.interfaces;

import javax.jws.WebService;

/**
 * Interface para acesso ao servi�o remoto de identifica��o
 * de cart�es de acesso ao ru.
 * 
 * @author geyson
 *
 */
@WebService
public interface IdentificacaoCartaoRemoteService {
	
	public long identificarCartaoRu(String cartaoRu);

}
