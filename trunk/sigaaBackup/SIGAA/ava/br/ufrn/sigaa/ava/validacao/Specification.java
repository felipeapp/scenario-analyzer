/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 18/01/2008
 */
package br.ufrn.sigaa.ava.validacao;


/**
 * Interface que informa o formato dos objetos que especificar�o os atributos necess�rios em objetos a serem cadastrados ou alterados.
 * 
 * @author David Pereira
 *
 */
public interface Specification {

	public boolean isSatisfiedBy(Object objeto);
	
	public Notification getNotification();
	
}
