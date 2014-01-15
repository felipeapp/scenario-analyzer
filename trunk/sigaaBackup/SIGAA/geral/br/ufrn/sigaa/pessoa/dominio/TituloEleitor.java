/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '13/09/2006'
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
import javax.persistence.Transient;

import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.UnidadeFederativa;

/**
 * Esta entidade armazena as informações dos documentos de título de eleitor das pessoas
 */
@Embeddable
public class TituloEleitor implements Serializable {

	/** número do título */
	private String numero;

	/** zona do título */
	private String zona;

	/** seção do título */
	private String secao;

	/** data de expedição */
	private Date dataExpedicao;

	/** unidade federativa do título */
	private UnidadeFederativa unidadeFederativa = new UnidadeFederativa();


	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_uf_titulo_eleitor")
	public UnidadeFederativa getUnidadeFederativa() {
		return unidadeFederativa;
	}

	public void setUnidadeFederativa(UnidadeFederativa unidadeFederativa) {
		this.unidadeFederativa = unidadeFederativa;
	}

	@Column(name = "data_expedicao_titulo_eleitor")
	public Date getDataExpedicao() {
		return dataExpedicao;
	}

	public void setDataExpedicao(Date dataExpedicao) {
		this.dataExpedicao = dataExpedicao;
	}

	@Column(name = "numero_titulo_eleitor")
	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	@Column(name = "secao_titulo_eleitor")
	public String getSecao() {
		return secao;
	}

	public void setSecao(String secao) {
		this.secao = secao;
	}

	@Column(name = "zona_titulo_eleitor")
	public String getZona() {
		return zona;
	}

	public void setZona(String zona) {
		this.zona = zona;
	}

	@Transient
	public boolean isEmpty() {
		return ValidatorUtil.isEmpty(numero) && ValidatorUtil.isEmpty(zona) && ValidatorUtil.isEmpty(secao);
	}

}
