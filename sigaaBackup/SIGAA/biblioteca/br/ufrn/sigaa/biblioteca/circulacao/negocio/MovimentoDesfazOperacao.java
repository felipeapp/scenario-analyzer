/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 26/11/2008
 *
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import br.ufrn.arq.dominio.MovimentoCadastro;

/**
 *
 *    Contem as informações necessárias para desfazer uma operação de empréstimo, renovação 
 * ou devolução;
 *
 * @author jadson
 * @since 26/11/2008
 * @version 1.0 criação da classe
 *
 */
@SuppressWarnings("serial")
public class MovimentoDesfazOperacao extends MovimentoCadastro{

	/** Id do empréstimo que está sendo desfeita a operação */
	private int idEmprestimo;   
	/** Tipo da operação a ser desfeita */
	private int tipoOperacao;   
	/** Quem autorizou a operação. */
	private int idAutorizador;  
	/** o operador que estava operando o sistema quando a operação foi desfeita. */
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
