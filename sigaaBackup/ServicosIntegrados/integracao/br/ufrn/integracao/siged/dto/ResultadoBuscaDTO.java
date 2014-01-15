/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 16/12/2009
*
*/

package br.ufrn.integracao.siged.dto;

import java.io.Serializable;

/**
 * Data Transfer Object utilizado para encapsular
 * os dados retornados por uma busca realizada no SIGED.
 * 
 * @author David Pereira
 *
 */
public class ResultadoBuscaDTO implements Serializable {

	/**
	 * Serial da versão
	 */
	private static final long serialVersionUID = 6628820673323531665L;

	/** DTO do Documento retornado pela busca */
	private DocumentoDTO documento;
	
	/** Texto que casa com a string de busca */
	private String texto;

	/** Relevância do resultado na busca */
	private double relevancia;

	public DocumentoDTO getDocumento() {
		return documento;
	}

	public void setDocumento(DocumentoDTO documento) {
		this.documento = documento;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public double getRelevancia() {
		return relevancia;
	}

	public void setRelevancia(double relevancia) {
		this.relevancia = relevancia;
	}
	
}
