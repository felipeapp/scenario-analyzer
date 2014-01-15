/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Essa classe � respons�vel pelo armazenamento do c�digo dos projetos de pesquisa.
 */
@Embeddable
public class CodigoProjetoPesquisa implements Serializable, Comparable<CodigoProjetoPesquisa> {

	/** Referente ao prefixo usado no projetos de pesquisa */
	@Column(name = "cod_prefixo", unique = false, nullable = true, insertable = true, updatable = true)
	String prefixo;

	/** N�mero que se refere ao c�digo do projeto de pesquisa */
	@Column(name = "cod_numero", unique = false, nullable = true, insertable = true, updatable = true)
	int numero;

	/** Referente ao c�digo do ano do projeto de pesquisa */
	@Column(name = "cod_ano", unique = false, nullable = true, insertable = true, updatable = true)
	int ano;

	/** Construtor padr�o. */
	public CodigoProjetoPesquisa() {

	}

	/** Construtor que recebe como argumento uma �nica String contendo o c�digo do projeto */
	public CodigoProjetoPesquisa(String codigo) {
		if ( codigo.length() < 11) {
			throw new IllegalArgumentException("C�digo inv�lido");
		}
		
		prefixo = codigo.substring(0, 3).toUpperCase();

		int hifen = codigo.indexOf('-');
		if (hifen == -1) {
			throw new IllegalArgumentException("C�digo inv�lido");
		}

		numero = Integer.valueOf(codigo.substring(3, hifen));
		ano = Integer.valueOf(codigo.substring(hifen + 1, codigo.length()).trim());
	}
	
	/** Construtor que recebe os campos que formam o c�digo do projeto separadamente */
	public CodigoProjetoPesquisa(String prefixo, int numero, int ano) {
		this.prefixo = prefixo;
		this.numero = numero;
		this.ano = ano;
	}

	/** Representa��o formatada do c�digo do projeto */
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
