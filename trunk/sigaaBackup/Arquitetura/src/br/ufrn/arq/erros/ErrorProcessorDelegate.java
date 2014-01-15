/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 15/08/2011
 */
package br.ufrn.arq.erros;

import java.util.Queue;

import br.ufrn.arq.erros.gerencia.MovimentoErro;

/**
 * Classe que registra os erros em uma fila para serem consumidos
 * posteriormente pela thread ErrorConsumer.
 * 
 * @author David Pereira
 *
 */
public class ErrorProcessorDelegate {

	private static ErrorProcessorDelegate instance = new ErrorProcessorDelegate();
	
	/** Fila em que os erros serão armazenados para serem posteriormente gravados no banco de dados. */
	private Queue<MovimentoErro> erros;
	
	private ErrorProcessorDelegate() { }
	
	public static ErrorProcessorDelegate getInstance() {
		return instance;
	}
	
	/**
	 * Adiciona um erro à fila de erros
	 * 
	 * @param log
	 */
	public void writeError(MovimentoErro erro) {
		synchronized (erros) {
			erros.add(erro);
			erros.notifyAll();
		}
	}
	
	public void setErros(Queue<MovimentoErro> erros) {
		this.erros = erros;
	}
	
}
