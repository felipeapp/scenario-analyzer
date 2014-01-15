/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 22/11/2010
 *
 */
package br.ufrn.integracao.siged.dto;

import java.io.Serializable;

/**
 * Data Transfer Object que contém as principais informações
 * associadas a um link de documento do SIGED.
 * 
 * @author Raphael Medeiros
 *
 */
public class LinkDocumentoDTO implements Serializable {

	/** Identificador do link para documento no SIGED. */
	private int id;
	
	/** Identificador do documento de destino do link. */
	private int destino;
	
	/** Tipo de documento para o qual está sendo cadastrado o link. */
	private int tipoDocumento;
	
	/** Pasta no siged em que o documento está localizado. */
	private int localizacao;
	
	/** Nome do link para o documento */
	private String nome;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDestino() {
		return destino;
	}

	public void setDestino(int destino) {
		this.destino = destino;
	}

	public int getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(int localizacao) {
		this.localizacao = localizacao;
	}

	public int getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(int tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
}