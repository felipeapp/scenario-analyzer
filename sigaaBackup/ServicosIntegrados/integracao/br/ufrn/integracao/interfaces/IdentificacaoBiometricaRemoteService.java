/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 05/04/2010
 */
package br.ufrn.integracao.interfaces;

import javax.jws.WebService;


/**
 * Interface para acesso ao servi�o remoto de identifica��o
 * de impress�es digitais.
 * 
 * @author David Pereira
 *
 */
@WebService
public interface IdentificacaoBiometricaRemoteService {

	public long identificar(byte[] digital);
	
}
