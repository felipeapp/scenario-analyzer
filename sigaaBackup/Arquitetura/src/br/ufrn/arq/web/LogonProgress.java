/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 18/02/2009
 */
package br.ufrn.arq.web;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


/**
 * Classe para marcar o status do logon do usuário.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Component @Scope("session")
public class LogonProgress implements Progressable {

	private int current;
	
	private int total;
	
	public int getCurrent() {
		return current;
	}

	public int getTotal() {
		return total;
	}

	public void increment() {
		current++;
	}
	
	public void reset() {
		current = 0;
	}

	public void setTotal(int total) {
		this.total = total;
	}
	
	public boolean isFinished() {
		return current == total;
	}

	public int getCurrentPercent() {
		return (int) ((current * 100.0)/ total);
	}

	public void incrementaTotal(int total) {
		this.total += total;
	}
	
}
