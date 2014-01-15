/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 25/10/2010
 *
 */
package br.ufrn.integracao.dto;


/**
 * DTO que representa a localidade de um bem, usado para comunicar o SIPAC com o dispositivo móvel
 * (Ex: Coletor de Dados) para realizar operações como o levantamento patrimonial.
 * @author Alessandro
 *
 */
public class LocalidadeDTO {

	/**
	 * Identificador
	 */
	private int id;
	
	/**
	 * Denominação da localidade de um bem.
	 */
	private String denominacao;
	
	
	/**
	 * Código da localidade de um bem.
	 */
	private String codigo;
	
	/**
	 * Referente ao id da unidade que a localidade é vinculada.
	 */
	private int idUnidade;
	
	
	
	
	
	public int getIdUnidade() {
		return idUnidade;
	}


	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}


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





	public String getCodigo() {
		return codigo;
	}



	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}





	public String getNomeCompleto() {
		return this.codigo + " - " + 	this.denominacao;
	}
	
}
