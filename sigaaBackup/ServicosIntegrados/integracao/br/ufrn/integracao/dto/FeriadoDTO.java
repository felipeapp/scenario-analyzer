/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 11/05/2011
 */
package br.ufrn.integracao.dto;

import java.util.Date;

/**
 * Data Transfer Object que contém as principais informações
 * associadas a um feriado.
 * 
 * @author David Pereira
 *
 */
public class FeriadoDTO {

	public static final char FERIADO = 'F';
	
	public static final char SUSPENSAO_ATIVIDADES = 'S';
	
	private String nome;
	
	private Date data;
	
	private char categoria;
	
	private String cidade;
	
	private String estado;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public char getCategoria() {
		return categoria;
	}

	public void setCategoria(char categoria) {
		this.categoria = categoria;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

}
