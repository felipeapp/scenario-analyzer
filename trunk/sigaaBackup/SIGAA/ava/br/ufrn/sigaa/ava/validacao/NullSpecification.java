/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 23/01/2008
 */
package br.ufrn.sigaa.ava.validacao;

/**
 * Especificação que indica que o objeto não precisa ser validado.
 * 
 * @author David Pereira
 *
 */
public class NullSpecification implements Specification {

	public Notification getNotification() {
		return new Notification();
	}

	public boolean isSatisfiedBy(Object objeto) {
		return true;
	}

}
