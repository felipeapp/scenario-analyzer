/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 05/04/2010
 */
package br.ufrn.integracao.interfaces;

import javax.jws.WebService;


/**
 * Interface para acesso ao serviço remoto de identificação
 * de impressões digitais.
 * 
 * @author David Pereira
 *
 */
@WebService
public interface IdentificacaoBiometricaRemoteService {

	public long identificar(byte[] digital);
	
}
