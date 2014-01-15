/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 02/09/2004
 */
package br.ufrn.arq.erros;

/**
 * Exceção geral para os erros de arquitetura
 * 
 * @author Gleydson Lima
 */
public class ArqException extends UFRNException {

	private boolean readable;
	
	public ArqException(String msg) {
		super(msg);
	}

	public ArqException(Exception e) {
		super(e);
	}

	public ArqException(String mensagem, Exception e) {
		super(mensagem, e);
	}

	public ArqException(int codErro, String msg) {
		super(codErro, msg);
	}

	public boolean isReadable() {
		return readable;
	}

	public void setReadable(boolean readable) {
		this.readable = readable;
	}

}