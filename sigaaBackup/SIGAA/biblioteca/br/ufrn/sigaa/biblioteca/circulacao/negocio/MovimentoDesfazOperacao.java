/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 26/11/2008
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import br.ufrn.arq.dominio.MovimentoCadastro;

/**
 *
 *    Contem as informa��es necess�rias para desfazer uma opera��o de empr�stimo, renova��o 
 * ou devolu��o;
 *
 * @author jadson
 * @since 26/11/2008
 * @version 1.0 cria��o da classe
 *
 */
@SuppressWarnings("serial")
public class MovimentoDesfazOperacao extends MovimentoCadastro{

	/** Id do empr�stimo que est� sendo desfeita a opera��o */
	private int idEmprestimo;   
	/** Tipo da opera��o a ser desfeita */
	private int tipoOperacao;   
	/** Quem autorizou a opera��o. */
	private int idAutorizador;  
	/** o operador que estava operando o sistema quando a opera��o foi desfeita. */
	private int idOperador;      
	
	public MovimentoDesfazOperacao(int tipoOperacao, int idEmprestimo, int idAutorizador, int idOperador){
		this.idEmprestimo = idEmprestimo;
		this.tipoOperacao = tipoOperacao;
		this.idAutorizador = idAutorizador;
		this.idOperador = idOperador;
	}

	public int getIdEmprestimo() {
		return idEmprestimo;
	}

	public int getTipoOperacao() {
		return tipoOperacao;
	}

	public int getIdAutorizador() {
		return idAutorizador;
	}

	public int getIdOperador() {
		return idOperador;
	}
	
	
	
}
