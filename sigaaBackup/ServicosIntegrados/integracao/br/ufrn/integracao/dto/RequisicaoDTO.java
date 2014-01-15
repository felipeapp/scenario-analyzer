/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on '12/03/2012'
 * 
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * Data Transfer Object para informa��es de requisi��es.
 * 
 * @author Tiago Hiroshi
 *
 */
public class RequisicaoDTO implements Serializable {

	/** Identificador da requisi��o. */
	private int id;
	/** N�mero da requisi��o. */
	private int numero;
	/** Ano da requisi��o. */
	private int ano;
	/** Nome do fornecedor da requisi��o. */
	private String nomeFornecedor;
	/** PIS/PASEP do fornecedor da requisi��o. */
	private Long pisFornecedor;
	/** Valor da requisi��o. */
	private double valor;

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		
		if(obj instanceof RequisicaoDTO)
			return ((RequisicaoDTO) obj).getId() == id;
		
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public String getNomeFornecedor() {
		return nomeFornecedor;
	}

	public void setNomeFornecedor(String nomeFornecedor) {
		this.nomeFornecedor = nomeFornecedor;
	}

	public Long getPisFornecedor() {
		return pisFornecedor;
	}

	public void setPisFornecedor(Long pisFornecedor) {
		this.pisFornecedor = pisFornecedor;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

}
