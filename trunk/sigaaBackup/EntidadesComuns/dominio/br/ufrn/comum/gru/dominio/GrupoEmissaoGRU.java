/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 09/12/2011
 *
 */
package br.ufrn.comum.gru.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;

/**
 * Configura��o de par�metros padr�es a serem utilizados na gera��o de uma GRU.<br/>
 * Esta classe define um conjunto de valores padr�es de:
 * <ul>
 * <li>unidade gestora (GRU simples)</li>
 * <li>gest�o (GRU simples)</li>
 * <li>ag�ncia (GRU cobran�a)</li>
 * <li>c�digo do cedente (GRU cobran�a)</li>
 * </ul>
 * c�digo de recolhimento a serem utilizados na gera��o de GRU como, por
 * exemplo, inscri��es em Processos Seletivos.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
@Entity
@Table(name = "grupo_emissao_gru", schema = "gru")
public class GrupoEmissaoGRU implements PersistDB {

	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "gru.gru_sequence") })
	@Column(name = "id_grupo_emissao_gru")
	private int id;
	
	/** C�digo de identifica��o do cedente da GRU. */
	@Column(name = "codigo_cedente")
	private String codigoCedente;
	
	/** C�digo da Gest�o da GRU. */
	@Column(name = "codigo_gestao")
	private String codigoGestao;

	/** Ag�ncia do emitente da GRU. */
	private String agencia;

	/** C�digo da Unidade Gestora da GRU. */
	@Column(name = "codigo_unidade_gestora")
	private String codigoUnidadeGestora;
	
	/** N�mero do conv�nio com o Banco do Brasil. */
	@Column(name = "convenio")
	private Integer convenio;

	/** Indica se esta configua��o est� ativa ou n�o no sistema. */
	@CampoAtivo
	private Boolean ativo;

	public GrupoEmissaoGRU() {
		ativo = true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public String getCodigoCedente() {
		return codigoCedente;
	}

	public void setCodigoCedente(String codigoCedente) {
		this.codigoCedente = codigoCedente;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getCodigoGestao() {
		return codigoGestao;
	}

	public void setCodigoGestao(String codigoGestao) {
		this.codigoGestao = codigoGestao;
	}

	public String getCodigoUnidadeGestora() {
		return codigoUnidadeGestora;
	}

	public void setCodigoUnidadeGestora(String codigoUnidadeGestora) {
		this.codigoUnidadeGestora = codigoUnidadeGestora;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Integer getConvenio() {
		return convenio;
	}

	public void setConvenio(Integer convenio) {
		this.convenio = convenio;
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		if (!isEmpty(agencia))
			str.append("Ag�ncia: ").append(agencia).append(", ");
		if (!isEmpty(codigoCedente))
			str.append("C�digo Cedente: ").append(codigoCedente).append(", ");
		if (!isEmpty(codigoGestao))
			str.append("C�digo da Gest�o: ").append(codigoGestao).append(", ");
		if (!isEmpty(codigoUnidadeGestora))
			str.append("C�digo da Unidade Gestora: ").append(codigoUnidadeGestora).append(", ");
		if (!isEmpty(convenio))
			str.append("Conv�nio: ").append(convenio).append(", ");
		if (str.lastIndexOf(", ") > 0)
			return str.substring(0, str.lastIndexOf(", "));
		else return "";
	}
}
