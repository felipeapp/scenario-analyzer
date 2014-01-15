/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/08/2008
 *
 */
package br.ufrn.sigaa.biblioteca.integracao.dtos;

import java.io.Serializable;

/**
 *   Classe criada para passar os dados de um t�tulo para o m�dulo desktop de circula��o.
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
	
	/** faz uma referencia a t�tulo real. */
	public int idTituloCatalografico;

	/**
     * Construtor default, obrigat�rios nos DTOs.
     */
	public TituloCatalograficoDto() {
		super();
	}

	/**
	 * Usado nas p�gina JSF
	 * @return
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * Usado nas p�gina JSF
	 * @return
	 */
	public String getAutor() {
		return autor;
	}
	
	
	
}