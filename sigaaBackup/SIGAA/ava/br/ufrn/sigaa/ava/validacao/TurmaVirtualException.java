/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 22/01/2008
 */
package br.ufrn.sigaa.ava.validacao;

/**
 * Exce��o a ser lan�ada por opera��es na turma virtual.
 * 
 * @author David Pereira
 *
 */
public class TurmaVirtualException extends RuntimeException {

	public TurmaVirtualException(String msg) {
		super(msg);
	}

	public TurmaVirtualException(Exception e) {
		super(e);
	}
	
}
