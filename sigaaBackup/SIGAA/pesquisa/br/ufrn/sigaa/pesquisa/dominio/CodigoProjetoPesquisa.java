/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Essa classe é responsável pelo armazenamento do código dos projetos de pesquisa.
 */
@Embeddable
public class CodigoProjetoPesquisa implements Serializable, Comparable<CodigoProjetoPesquisa> {

	/** Referente ao prefixo usado no projetos de pesquisa */
	@Column(name = "cod_prefixo", unique = false, nullable = true, insertable = true, updatable = true)
	String prefixo;

	/** Número que se refere ao código do projeto de pesquisa */
	@Column(name = "cod_numero", unique = false, nullable = true, insertable = true, updatable = true)
	int numero;

	/** Referente ao código do ano do projeto de pesquisa */
	@Column(name = "cod_ano", unique = false, nullable = true, insertable = true, updatable = true)
	int ano;

	/** Construtor padrão. */
	public CodigoProjetoPesquisa() {

	}

	/** Construtor que recebe como argumento uma única String contendo o código do projeto */
	public CodigoProjetoPesquisa(String codigo) {
		if ( codigo.length() < 11) {
			throw new IllegalArgumentException("Código inválido");
		}
		
		prefixo = codigo.substring(0, 3).toUpperCase();

		int hifen = codigo.indexOf('-');
		if (hifen == -1) {
			throw new IllegalArgumentException("Código inválido");
		}

		numero = Integer.valueOf(codigo.substring(3, hifen));
		ano = Integer.valueOf(codigo.substring(hifen + 1, codigo.length()).trim());
	}
	
	/** Construtor que recebe os campos que formam o código do projeto separadamente */
	public CodigoProjetoPesquisa(String prefixo, int numero, int ano) {
		this.prefixo = prefixo;
		this.numero = numero;
		this.ano = ano;
	}

	/** Representação formatada do código do projeto */
	@Override
	public String toString() {
		return (prefixo != null ? prefixo + numero + "-" + ano : "");
	}

	/**
	 * @return the prefixo
	 */

	public String getPrefixo() {
		return prefixo;
	}

	/**
	 * @return the numero
	 */
	public int getNumero() {
		return numero;
	}

	/**
	 * @return the ano
	 */
	public int getAno() {
		return ano;
	}

	/**
	 * @param ano the ano to set
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/**
	 * @param prefixo the prefixo to set
	 */
	public void setPrefixo(String prefixo) {
		this.prefixo = prefixo;
	}

	/**
	 * @param numero the numero to set
	 */
	public void setNumero(int numero) {
		this.numero = numero;
	}

	public int compareTo(CodigoProjetoPesquisa o) {
		int result = new Integer(numero).compareTo(o.getNumero());

		if (result == 0) {
			result = new Integer(ano).compareTo(o.getAno());
		}

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testTransientEquals(this, obj, "prefixo", "numero", "ano");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(prefixo, numero, ano);
	}
}
