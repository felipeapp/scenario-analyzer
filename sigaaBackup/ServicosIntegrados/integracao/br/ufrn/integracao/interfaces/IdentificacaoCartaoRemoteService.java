/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 16/02/2011
 */
package br.ufrn.integracao.interfaces;

import javax.jws.WebService;

/**
 * Interface para acesso ao serviço remoto de identificação
 * de cartões de acesso ao ru.
 * 
 * @author geyson
 *
 */
@WebService
public interface IdentificacaoCartaoRemoteService {
	
	public long identificarCartaoRu(String cartaoRu);

}
