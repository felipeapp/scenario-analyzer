/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;


/**
 * Classe que representa os financiamentos de projetos de pesquisa
 * 
 * @author Ricardo Wendell
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "financiamento_projeto_pesq", schema = "pesquisa", uniqueConstraints = {})
public class FinanciamentoProjetoPesq implements PersistDB {

	// Fields

	private int id;

	/** Projeto de pequisa ligada ao financiamento do projeto */
	private ProjetoPesquisa projetoPesquisa;

	/** Entidade que financiadora do projeto */
	private EntidadeFinanciadora entidadeFinanciadora = new EntidadeFinanciadora();

	/** Natureza do financiamento do projeto */
	private TipoNaturezaFinanciamento tipoNaturezaFinanciamento = new TipoNaturezaFinanciamento();

	/** Data de início do financiamento */
	private Date dataInicio;

	/** Data final do financiamento do projeto */
	private Date dataFim;

	// Constructors

	/** default constructor */
	public FinanciamentoProjetoPesq() {
	}

	/** minimal constructor */
	public FinanciamentoProjetoPesq(int idFinanciamentoProjetoPesq) {
		this.id = idFinanciamentoProjetoPesq;
	}

	// Property accessors
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_financiamento_projeto_pesq", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idFinanciamentoProjetoPesq) {
		this.id = idFinanciamentoProjetoPesq;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_projeto_pesquisa", unique = false, nullable = true, insertable = true, updatable = true)
	public ProjetoPesquisa getProjetoPesquisa() {
		return this.projetoPesquisa;
	}

	public void setProjetoPesquisa(ProjetoPesquisa projetoPesquisa) {
		this.projetoPesquisa = projetoPesquisa;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_natureza_financ", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoNaturezaFinanciamento getTipoNaturezaFinanciamento() {
		return this.tipoNaturezaFinanciamento;
	}

	public void setTipoNaturezaFinanciamento(TipoNaturezaFinanciamento tipoNaturezaFinanciamento) {
		this.tipoNaturezaFinanciamento = tipoNaturezaFinanciamento;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataInicio() {
		return this.dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_fim", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataFim() {
		return this.dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testTransientEquals(this, obj, "id", "dataFim", "dataInicio", "entidadeFinanciadora");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, dataFim, dataInicio, entidadeFinanciadora);
	}

	/**
	 * @return the entidadeFinanciadora
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_entidade_financiadora", unique = false, nullable = true, insertable = true, updatable = true)
	public EntidadeFinanciadora getEntidadeFinanciadora() {
		return entidadeFinanciadora;
	}

	/**
	 * @param entidadeFinanciadora the entidadeFinanciadora to set
	 */
	public void setEntidadeFinanciadora(EntidadeFinanciadora entidadeFinanciadora) {
		this.entidadeFinanciadora = entidadeFinanciadora;
	}

}
