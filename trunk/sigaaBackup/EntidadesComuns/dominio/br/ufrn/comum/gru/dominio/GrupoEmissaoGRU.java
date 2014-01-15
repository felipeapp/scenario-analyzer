/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Configuração de parâmetros padrões a serem utilizados na geração de uma GRU.<br/>
 * Esta classe define um conjunto de valores padrões de:
 * <ul>
 * <li>unidade gestora (GRU simples)</li>
 * <li>gestão (GRU simples)</li>
 * <li>agência (GRU cobrança)</li>
 * <li>código do cedente (GRU cobrança)</li>
 * </ul>
 * código de recolhimento a serem utilizados na geração de GRU como, por
 * exemplo, inscrições em Processos Seletivos.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
@Entity
@Table(name = "grupo_emissao_gru", schema = "gru")
public class GrupoEmissaoGRU implements PersistDB {

	/** Chave primária. */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "gru.gru_sequence") })
	@Column(name = "id_grupo_emissao_gru")
	private int id;
	
	/** Código de identificação do cedente da GRU. */
	@Column(name = "codigo_cedente")
	private String codigoCedente;
	
	/** Código da Gestão da GRU. */
	@Column(name = "codigo_gestao")
	private String codigoGestao;

	/** Agência do emitente da GRU. */
	private String agencia;

	/** Código da Unidade Gestora da GRU. */
	@Column(name = "codigo_unidade_gestora")
	private String codigoUnidadeGestora;
	
	/** Número do convênio com o Banco do Brasil. */
	@Column(name = "convenio")
	private Integer convenio;

	/** Indica se esta configuação está ativa ou não no sistema. */
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
			str.append("AgÍncia: ").append(agencia).append(", ");
		if (!isEmpty(codigoCedente))
			str.append("CÛdigo Cedente: ").append(codigoCedente).append(", ");
		if (!isEmpty(codigoGestao))
			str.append("CÛdigo da Gest„o: ").append(codigoGestao).append(", ");
		if (!isEmpty(codigoUnidadeGestora))
			str.append("CÛdigo da Unidade Gestora: ").append(codigoUnidadeGestora).append(", ");
		if (!isEmpty(convenio))
			str.append("ConvÍnio: ").append(convenio).append(", ");
		if (str.lastIndexOf(", ") > 0)
			return str.substring(0, str.lastIndexOf(", "));
		else return "";
	}
}
