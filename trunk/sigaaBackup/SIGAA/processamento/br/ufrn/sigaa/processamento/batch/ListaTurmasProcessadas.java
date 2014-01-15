/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 11/01/2008 
 *
 */

package br.ufrn.sigaa.processamento.batch;

import java.util.Stack;

import br.ufrn.sigaa.processamento.dominio.TurmaProcessada;

/**
 * Lista de turmas processadas para gerar os arquivos com
 * o resultado do processamento. 
 * 
 * @author David Pereira
 *
 */
public class ListaTurmasProcessadas {

	static Stack<TurmaProcessada> turmas = new Stack<TurmaProcessada>();
	
	public static synchronized void addTurma(TurmaProcessada turma) {
		turmas.add(turma);
	}
	
	public static synchronized TurmaProcessada getTurma() {
		return turmas.pop();
	}
	
	public static synchronized boolean hasTurma() { 
		return !turmas.isEmpty();
	}
	
	public static synchronized int count() {
		return turmas.size();
	}
	
}
