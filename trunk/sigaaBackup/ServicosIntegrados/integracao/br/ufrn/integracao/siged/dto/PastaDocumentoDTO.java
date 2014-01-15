/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/03/2010
 */
package br.ufrn.integracao.siged.dto;

import java.io.Serializable;

/**
 * Data Transfer Object que contém as principais informações
 * associadas a uma pasta de documentos do SIGED.
 * 
 * @author David Pereira
 *
 */
public class PastaDocumentoDTO implements Serializable {

	/**
	 * Serial de versão
	 */
	private static final long serialVersionUID = 685045111006411736L;

	/** Identificador da pasta de documentos no SIGED */
	private int id;
	
	/** Identificador da pasta pai. Null, se estiver na raiz. */
	private Integer pai;
	
	/** Nome da pasta */
	private String nome;

	/** Label da pasta */
	private String label;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getPai() {
		return pai;
	}

	public void setPai(Integer pai) {
		this.pai = pai;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
}
