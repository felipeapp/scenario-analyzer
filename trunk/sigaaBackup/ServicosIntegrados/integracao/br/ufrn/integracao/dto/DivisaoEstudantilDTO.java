package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * Classe responsavel pela transferencia de dados da DivisaoEstudantil entre sistemas.
 * 
 * @author Raphael Medeiros
 *
 */
public class DivisaoEstudantilDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	
	/** Nome da divisão. */
	private String nome;
	
	/** Indica se esta divisão está ativa no sistema. */
	private boolean ativo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
}