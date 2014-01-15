/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * Data Transfer Object para tratar da transfer�ncia
 * das informa��es de unidades entre sistemas.
 * 
 * @author David Pereira
 *
 */
public class UnidadeDTO implements Serializable {
	
	private static final long serialVersionUID = -1L;
	
	/**
	 * Identificador
	 */
	private int id;
	
	/**
	 * Vari�vel que representa o c�digo da unidade.
	 */
	private long codigoUnidade;

	/**
	 * Vari�vel que representa a sigla da unidade.
	 */
	private String sigla;

	/**
	 * Vari�vel que representa o nome da unidade.
	 */
	private String denominacao;
	
	/**
	 * Vari�vel que representa a hierarquia da unidade, pode ser organizacional ou n�o.
	 */
	private String hierarquia;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getCodigoUnidade() {
		return codigoUnidade;
	}

	public void setCodigoUnidade(long codigoUnidade) {
		this.codigoUnidade = codigoUnidade;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getHierarquia() {
		return hierarquia;
	}

	public void setHierarquia(String hierarquia) {
		this.hierarquia = hierarquia;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	@Override
	public String toString() {
		return getDenominacao();
	} 
	
}
