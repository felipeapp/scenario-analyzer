package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;

import br.ufrn.arq.util.EqualsUtil;

/**
 * Define a classe de transfer�ncia representativa de uma manifesta��o a ouvidoria
 * 
 * @author Bernardo
 *
 */
public class ManifestacaoDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/** Identificador */
	private int id;
	
	/** T�tulo da manifesta��o. */
	private String titulo;
	
	/** N�mero atribu�do � manifesta��o. */
	private String numero;
	
	/** Ano de cadastro da manifesta��o. */
	private int ano;
	
	/** Prazo definido para resposta da manifesta��o. */
	private Date prazo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public Date getPrazo() {
		return prazo;
	}

	public void setPrazo(Date prazo) {
		this.prazo = prazo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

}
