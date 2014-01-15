/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '01/12/2006'
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Dados do documento militar   
 */
@Embeddable
public class CertificadoMilitar implements Serializable{

	/** n�mero do documento militar */
	private String numero;

	/** s�rio do documento */
	private String serie;

	/** categoria do documento */
	private String categoria;

	/** �rg�o que expediu o documento */
	private String orgaoExpedicao;

	/** data de expedi��o */
	private Date dataExpedicao;

	@Column(name = "categoria_certificado_militar")
	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	@Column(name = "data_expedicao_certificado_militar")
	public Date getDataExpedicao() {
		return dataExpedicao;
	}

	public void setDataExpedicao(Date dataExpedicao) {
		this.dataExpedicao = dataExpedicao;
	}

	@Column(name = "numero_certificado_militar")
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	@Column(name = "orgao_expedicao_certificado_militar")
	public String getOrgaoExpedicao() {
		return orgaoExpedicao;
	}

	public void setOrgaoExpedicao(String orgaoExpedicao) {
		this.orgaoExpedicao = orgaoExpedicao;
	}

	@Column(name = "serie_certificado_militar")
	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

}
