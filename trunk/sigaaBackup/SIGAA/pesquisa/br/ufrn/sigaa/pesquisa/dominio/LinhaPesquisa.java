/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;


/**
 * Representa as linhas de pesquisa que um projeto de pesquisa pode seguir
 * 
 * @author ricardo
 */
@Entity
@Table(name = "linha_pesquisa", schema = "pesquisa", uniqueConstraints = {})
public class LinhaPesquisa implements Validatable {

	// Fields

	private int id;
	/** Grupo de pesquisa ao qual a linha se refere, no caso de linhas vinculadas. */
	private GrupoPesquisa grupoPesquisa;
	/** Nome da linha de pesquisa */
	private String nome;
	/** Data em que a linha de pesquisa começou a ser utilizada */
	private Date dataInicio;

	private Set<ProjetoPesquisa> projetosPesquisa = new HashSet<ProjetoPesquisa>(0);
	/** Define se a linha foi inativada. */
	private boolean inativa;

	// Constructors

	/** default constructor */
	public LinhaPesquisa() {
		grupoPesquisa = new GrupoPesquisa();
	}

	/** default minimal constructor */
	public LinhaPesquisa(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public LinhaPesquisa(int idLinhaPesquisa, String nome) {
		this.id = idLinhaPesquisa;
		this.nome = nome;
	}

	/** full constructor */
	public LinhaPesquisa(int idLinhaPesquisa,
			GrupoPesquisa grupoPesquisa, String nome,
			Date dataInicio, Set<ProjetoPesquisa> projetoPesquisas) {
		this.id = idLinhaPesquisa;
		this.grupoPesquisa = grupoPesquisa;
		this.nome = nome;
		this.dataInicio = dataInicio;
		this.projetosPesquisa = projetoPesquisas;
	}

	// Property accessors
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_linha_pesquisa", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idLinhaPesquisa) {
		this.id = idLinhaPesquisa;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_grupo_pesquisa", unique = false, nullable = true, insertable = true, updatable = true)
	public GrupoPesquisa getGrupoPesquisa() {
		return this.grupoPesquisa;
	}

	public void setGrupoPesquisa(GrupoPesquisa grupoPesquisa) {
		this.grupoPesquisa = grupoPesquisa;
	}

	@Column(name = "nome", unique = false, nullable = false, insertable = true, updatable = true, length = 120)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "data_inicio", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	public Date getDataInicio() {
		return this.dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@OneToMany(cascade = { }, fetch = FetchType.LAZY, mappedBy = "linhaPesquisa")
	public Set<ProjetoPesquisa> getProjetosPesquisa() {
		return this.projetosPesquisa;
	}

	public void setProjetosPesquisa(Set<ProjetoPesquisa> projetoPesquisas) {
		this.projetosPesquisa = projetoPesquisas;
	}

	@Column(name = "inativa", unique = false, nullable = false, insertable = true, updatable = true)
	public boolean isInativa() {
		return inativa;
	}

	public void setInativa(boolean inativa) {
		this.inativa = inativa;
	}

	@Transient
	public Collection<ProjetoPesquisa> getProjetosPesquisaCol(){
		return new ArrayList<ProjetoPesquisa>(this.projetosPesquisa);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id", "nome");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, nome);
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(nome, "nome", lista);
		ValidatorUtil.validateRequiredId(grupoPesquisa.getId(), "Grupo Pesquisa", lista);
		return lista;
	}
	
}
