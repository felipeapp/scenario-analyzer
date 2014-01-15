/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.extensao.dominio.PlanoTrabalhoExtensao;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;

/**
 * Armazena os cronogramas de atividades referentes a projetos de pesquisa e planos de trabalho
 * Utilizada tanto em Pesquisa quanto em Extensão.
 * 
 * @author Victor Hugo
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "cronograma_projeto", schema = "projetos")
public class CronogramaProjeto implements PersistDB {

	/** Identificador do cronograma do projeto. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_cronograma_projeto")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

	/** Ordem de exibição do cronograma no formulário de cadastro. */
	@Column(name = "ordem", unique = true, nullable = false, insertable = true, updatable = true)
	private int ordem;
	
	/** Projeto ao qual o cronograma está vinculado. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "id_projeto")
	private Projeto projeto;

	/** Plano de Trabalho de Pesquisa ao qual o cronograma está vinculado. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_plano_trabalho", unique = false, nullable = true, insertable = true, updatable = true)
	private PlanoTrabalho planoTrabalho;
	
	/** Plano de Trabalho de Extensão ao qual o cronograma está vinculado. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_plano_trabalho_extensao", unique = false, nullable = true, insertable = true, updatable = true)
	private PlanoTrabalhoExtensao planoTrabalhoExtensao;
	
	/** Plano de trabalho de Projetos ao qual o cronograma está vinculado. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_plano_trabalho_projeto", unique = false, nullable = true, insertable = true, updatable = true)
	private PlanoTrabalhoProjeto planoTrabalhoProjeto;

	/** Descrição do plano de trabalho. */
	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true)
	private String descricao;
	
	/** Intervalos de execução do plano. */
	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, mappedBy = "cronograma")
	private Set<IntervaloCronograma> intervalos = new HashSet<IntervaloCronograma>(0);
	
	
	/** Construtor padrão.	 */
	public CronogramaProjeto() {
	}

	/** Construtor simples. */
	public CronogramaProjeto(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public PlanoTrabalho getPlanoTrabalho() {
		return planoTrabalho;
	}

	public void setPlanoTrabalho(PlanoTrabalho planoTrabalho) {
		this.planoTrabalho = planoTrabalho;
	}
	
	public PlanoTrabalhoExtensao getPlanoTrabalhoExtensao() {
		return planoTrabalhoExtensao;
	}

	public void setPlanoTrabalhoExtensao(PlanoTrabalhoExtensao planoTrabalhoExtensao) {
		this.planoTrabalhoExtensao = planoTrabalhoExtensao;
	}
	
	public PlanoTrabalhoProjeto getPlanoTrabalhoProjeto() {
		return planoTrabalhoProjeto;
	}

	public void setPlanoTrabalhoProjeto(PlanoTrabalhoProjeto planoTrabalhoProjeto) {
		this.planoTrabalhoProjeto = planoTrabalhoProjeto;
	}

	
	public Set<IntervaloCronograma> getIntervalos() {
		return intervalos;
	}

	public void setIntervalos(Set<IntervaloCronograma> intervalos) {
		this.intervalos = intervalos;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "descricao", "ordem");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, descricao);
	}

	public Projeto getProjeto() {
	    return projeto;
	}

	public void setProjeto(Projeto projeto) {
	    this.projeto = projeto;
	}

}
