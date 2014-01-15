/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 11/04/2011
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * Data Transfer Object para a troca de informações
 * de cursos entre sistemas.
 * 
 * @author David Pereira
 *
 */
public class CursoDTO implements Serializable {

	private int id;
	
	private String denominacao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}
	
}
