/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/08/2008
 *
 */
package br.ufrn.sigaa.biblioteca.integracao.dtos;

import java.io.Serializable;

/**
 *   Classe criada para passar os dados de um título para o módulo desktop de circulação.
 *
 * @author jadson
 * @since 22/08/2008
 * @version 1.0 criacao da classe
 *
 */
public class TituloCatalograficoDto implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** Campo 245$a bibliografico. */
	public String titulo;
	
	/** Campos 100$a bibliografico. */
	public String autor;
	
	/** Campo 260$c bibliografico. */
	public String ano;
	
	/** Campo 250$a . */
	public String edicao;
	
	/** faz uma referencia a título real. */
	public int idTituloCatalografico;

	/**
     * Construtor default, obrigatórios nos DTOs.
     */
	public TituloCatalograficoDto() {
		super();
	}

	/**
	 * Usado nas página JSF
	 * @return
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * Usado nas página JSF
	 * @return
	 */
	public String getAutor() {
		return autor;
	}
	
	
	
}