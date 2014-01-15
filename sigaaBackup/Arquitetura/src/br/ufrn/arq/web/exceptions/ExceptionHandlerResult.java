/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 23/04/2004
 */
package br.ufrn.arq.web.exceptions;

/**
 * Enum com os possíveis resultados de um exception handler.
 * 
 * @author Gleydson Lima
 * @author David Pereira
 *
 */
public enum ExceptionHandlerResult {

	/** Continuar o processamento, o nó da cadeia não pôde fazer nada com a exceção. */
	CONTINUE,
	
	/** Parar o processamento, algo já foi feito com a exceção. */
	STOP,

	/** Parar o processamento, ignorar a exceção. */
	IGNORE;
	
	public boolean isContinue() {
		return this == CONTINUE;
	}

	public boolean isIgnore() {
		return this == IGNORE;
	}

	public boolean isStop() {
		return this == STOP;
	}
	
}
