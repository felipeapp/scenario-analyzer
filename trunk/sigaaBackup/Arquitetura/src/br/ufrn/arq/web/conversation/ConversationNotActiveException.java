/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 12/02/2009
 */
package br.ufrn.arq.web.conversation;

/**
 * Exceção disparada quando o usuário tenta acessar um método
 * de um managed bean com escopo conversation e a conversação
 * ainda não foi iniciada ou foi finalizada.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class ConversationNotActiveException extends RuntimeException {

	public ConversationNotActiveException(String msg) {
		super(msg);
	}
	
}
