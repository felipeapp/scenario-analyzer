/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '01/12/06'
 *
 */
package br.ufrn.sigaa.pessoa.dominio;


import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import br.ufrn.sigaa.dominio.UnidadeFederativa;


/**
 * Entidade que armazena os dados do documento de identidade das pessoas  
 */
@Embeddable
public class Identidade implements Serializable {

	/** número da identidade */
	private String numero;

	/** Órgão expedidor */
	private String orgaoExpedicao;

	/** data de expedição */
	private Date dataExpedicao;

	/** unidade federativa do documento */
	private UnidadeFederativa unidadeFederativa;

	// Constructors

	/** default constructor */
	public Identidade() {
	}

	public void setDataExpedicao(Date dataExpedicao) {
		this.dataExpedicao = dataExpedicao;
	}

	@Column(name = "numero_identidade")
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	@Column(name = "orgao_expedicao_identidade")
	public String getOrgaoExpedicao() {
		return orgaoExpedicao;
	}

	public void setOrgaoExpedicao(String orgaoExpedicao) {
		this.orgaoExpedicao = orgaoExpedicao;
	}

	@Column(name = "data_expedicao_identidade")
	public Date getDataExpedicao() {
		return dataExpedicao;
	}

	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_uf_identidade")
	public UnidadeFederativa getUnidadeFederativa() {
		return unidadeFederativa;
	}

	public void setUnidadeFederativa(UnidadeFederativa unidadeFederativa) {
		this.unidadeFederativa = unidadeFederativa;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(numero);
		sb.append(" ");
		sb.append(orgaoExpedicao);
		
		if (unidadeFederativa != null)
			sb.append("/" + unidadeFederativa.getSigla());
		
		return sb.toString();
	}

}