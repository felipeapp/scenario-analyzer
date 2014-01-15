/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2009
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Código utilizado para identificar as notificações de invenção
 * 
 * @author Leonardo Campos
 *
 */
@Embeddable
public class CodigoNotificacaoInvencao implements Serializable, Comparable<CodigoNotificacaoInvencao> {
	
	@Column(name = "cod_prefixo", unique = false, nullable = false, insertable = true, updatable = true)
	String prefixo;

	@Column(name = "cod_numero", unique = false, nullable = false, insertable = true, updatable = true)
	int numero;

	@Column(name = "cod_ano", unique = false, nullable = false, insertable = true, updatable = true)
	int ano;
	
	public CodigoNotificacaoInvencao() {
		
	}

	@Override
	public String toString() {
		return (prefixo != null ? prefixo + numero + "-" + ano : null);
	}

	public String getPrefixo() {
		return prefixo;
	}

	public void setPrefixo(String prefixo) {
		this.prefixo = prefixo;
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
	
	public int compareTo(CodigoNotificacaoInvencao o) {
		int result = new Integer(numero).compareTo(o.getNumero());
		
		if (result == 0) {
			result = new Integer(ano).compareTo(o.getAno());
		}
		
		return result;
	}
}
