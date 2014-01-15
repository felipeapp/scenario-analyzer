/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 12/01/2009 
 *
 */

package br.ufrn.sigaa.processamento.batch;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Stack;

import br.ufrn.sigaa.processamento.dao.ProcessamentoMatriculaDao;

/**
 * Estrutura de dados para guardar as listas de informações
 * que serão processadas em batch pelo processamento de 
 * matrículas.
 * 
 * @author David Pereira
 *
 */
public abstract class ListaProcessamentoBatch<T> {

	Stack<T> lista = new Stack<T>();
	
	public int total;
	
	public int processados;
	
	public synchronized boolean possuiElementos() {
		return !isEmpty(lista);
	}
	
	public synchronized T getProximoElemento() {
		return lista.pop();
	}
	
	public synchronized void registraProcessado() {
		processados++;
	}
	
	public abstract void carregar(int ano, int periodo, boolean rematricula, ProcessamentoMatriculaDao dao);
	
}
