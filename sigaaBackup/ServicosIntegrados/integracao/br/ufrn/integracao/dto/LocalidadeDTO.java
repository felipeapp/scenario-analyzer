/*
 * Universidade Federal do Rio Grande do Norte

 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 * 
 * Created on 25/10/2010
 *
 */
package br.ufrn.integracao.dto;


/**
 * DTO que representa a localidade de um bem, usado para comunicar o SIPAC com o dispositivo m�vel
 * (Ex: Coletor de Dados) para realizar opera��es como o levantamento patrimonial.
 * @author Alessandro
 *
 */
public class LocalidadeDTO {

	/**
	 * Identificador
	 */
	private int id;
	
	/**
	 * Denomina��o da localidade de um bem.
	 */
	private String denominacao;
	
	
	/**
	 * C�digo da localidade de um bem.
	 */
	private String codigo;
	
	/**
	 * Referente ao id da unidade que a localidade � vinculada.
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
