/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 04/10/2007
 */
package br.ufrn.arq.erros;

/**
 *
 * Exceção utilizada para informar violação de acesso
 * para hosts não autorizados.
 *
 * @author Gleydson Lima
 *
 */

public class HostNaoAutorizadoException extends ArqException {

	public HostNaoAutorizadoException(String msg) {
		super(msg);
		setNotificavel(false);
	}

}
