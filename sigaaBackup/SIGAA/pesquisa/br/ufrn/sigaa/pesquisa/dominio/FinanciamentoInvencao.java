/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/05/2008
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Classe que registra as informações sobre os financiamentos obtidos pelos projetos de inovação
 * junto às entidades financiadoras
 * 
 * @author Leonardo Campos
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name="financiamento_invencao", schema="pesquisa", uniqueConstraints = {})
public class FinanciamentoInvencao implements Validatable {

	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name="id_financiamento_invencao", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Invenção que recebeu o financiamento */
	@ManyToOne
	@JoinColumn(name="id_invencao")
	private Invencao invencao;
	
	/** Entidade financiadora */
	@ManyToOne
	@JoinColumn(name="id_entidade_financiadora")
	private EntidadeFinanciadora entidadeFinanciadora = new EntidadeFinanciadora();
	
	/** Indica se houve comprometimento da propriedade intelectual da invenção */
	@Column(name="comprometimento_propriedade_intelectual")
	private boolean comprometimentoPropriedadeIntelectual;
	
	/** Descrição do contrato do financiamento */
	@Column(name="descricao_contrato")
	private String descricaoContrato;
	
	/** Valor do financiamento */
	private double valor;
	
	/** Título do Projeto junto à entidade financiadora */
	@Column(name="titulo_projeto")
	private String tituloProjeto;
	
	/** Código do Projeto junto à entidade financiadora */
	@Column(name="codigo_projeto")
	private String codigoProjeto;
	
	/** Indica se há co-titularidade da invenção devido ao financiamento */
	private Boolean cotitularidade = false;
	
	/** Número do processo do convênio do financiamento */
	@Column(name="numero_processo_convenio")
	private Integer numeroProcessoConvenio;
	
	public FinanciamentoInvencao() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Invencao getInvencao() {
		return invencao;
	}

	public void setInvencao(Invencao invencao) {
		this.invencao = invencao;
	}

	public EntidadeFinanciadora getEntidadeFinanciadora() {
		return entidadeFinanciadora;
	}

	public void setEntidadeFinanciadora(EntidadeFinanciadora entidadeFinanciadora) {
		this.entidadeFinanciadora = entidadeFinanciadora;
	}

	public boolean isComprometimentoPropriedadeIntelectual() {
		return comprometimentoPropriedadeIntelectual;
	}

	public void setComprometimentoPropriedadeIntelectual(
			boolean comprometimentoPropriedadeIntelectual) {
		this.comprometimentoPropriedadeIntelectual = comprometimentoPropriedadeIntelectual;
	}

	public String getDescricaoContrato() {
		return descricaoContrato;
	}

	public void setDescricaoContrato(String descricaoContrato) {
		this.descricaoContrato = descricaoContrato;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id"); 
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public ListaMensagens validate() {
		return null;
	}

	public String getTituloProjeto() {
		return tituloProjeto;
	}

	public void setTituloProjeto(String tituloProjeto) {
		this.tituloProjeto = tituloProjeto;
	}

	public String getCodigoProjeto() {
		return codigoProjeto;
	}

	public void setCodigoProjeto(String codigoProjeto) {
		this.codigoProjeto = codigoProjeto;
	}

	public boolean isCotitularidade() {
		return cotitularidade;
	}

	public void setCotitularidade(boolean cotitularidade) {
		this.cotitularidade = cotitularidade;
	}

	public Boolean getCotitularidade() {
		return cotitularidade;
	}

	public void setCotitularidade(Boolean cotitularidade) {
		this.cotitularidade = cotitularidade;
	}

	public Integer getNumeroProcessoConvenio() {
		return numeroProcessoConvenio;
	}

	public void setNumeroProcessoConvenio(Integer numeroProcessoConvenio) {
		this.numeroProcessoConvenio = numeroProcessoConvenio;
	}
}
