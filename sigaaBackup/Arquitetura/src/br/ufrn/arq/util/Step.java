/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/09/2006
 */
package br.ufrn.arq.util;

/**
 * Classe que representa um passo de um caso de uso
 * formado por vários passos em Struts.
 * 
 * @author Gleydson Lima
 *
 */
public class Step {

	private String nome;

	private String view;

	public Step(String nome, String view) {
		this.nome = nome;
		this.view = view;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}

}
