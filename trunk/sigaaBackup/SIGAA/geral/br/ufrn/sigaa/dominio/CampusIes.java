/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/09/2006
 *
 */
package br.ufrn.sigaa.dominio;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.ensino.dominio.TipoCampusUnidade;
import br.ufrn.sigaa.pessoa.dominio.Endereco;

/**
 * Classe que representa um campus da Instituição de Ensino Superior 
 */
@Entity
@Table(schema="comum", name = "campus_ies", uniqueConstraints = {})
@Cache ( usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE )
public class CampusIes implements Validatable {

	@Transient
	public static final int CAMPUS_UFRN = 1;
	
	// Fields    

	private int id;

	/**
	 * Tipo de campus
	 */
	@Deprecated
	private TipoCampusUnidade tipoCampusUnidade;
	
	/**
	 * Instituição de Ensino onde fica locazlizado o campus
	 */
	private InstituicoesEnsino instituicao;

	/**
	 * Campus pai, caso exista
	 */
	private CampusIes campusIes;

	/**
	 * Endereço do campus
	 */
	private Endereco endereco = new Endereco();

	/**
	 * Sigla do campus, usado nos formulários
	 */
	private String sigla;

	/**
	 * Nome do campus
	 */
	private String nome;

	/** Campo de controle utilizado nas jsp's */
	private boolean selecionado;
	
	// Constructors

	/** default constructor */
	public CampusIes() {
	}

	/** default minimal constructor */
	public CampusIes(int id) {
		this.id = id;
	}
	
	/** minimal constructor */
	public CampusIes(int idCampus, String sigla, String nome) {
		this.id = idCampus;
		this.sigla = sigla;
		this.nome = nome;
	}

	/** full constructor */
	public CampusIes(int idCampus, TipoCampusUnidade tipoCampusUnidade,
			CampusIes campusIes, Endereco endereco, String sigla, String nome) {
		this.id = idCampus;
		this.tipoCampusUnidade = tipoCampusUnidade;
		this.campusIes = campusIes;
		this.endereco = endereco;
		this.sigla = sigla;
		this.nome = nome;
	}

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", 
				parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_campus", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idCampus) {
		this.id = idCampus;
	}

	@ManyToOne
	@JoinColumn(name = "id_tipo_campus_unidade", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoCampusUnidade getTipoCampusUnidade() {
		return this.tipoCampusUnidade;
	}

	public void setTipoCampusUnidade(TipoCampusUnidade tipoCampusUnidade) {
		this.tipoCampusUnidade = tipoCampusUnidade;
	}

	@ManyToOne
	@JoinColumn(name = "id_campus_unidade_sede", unique = false, nullable = true, insertable = true, updatable = true)
	public CampusIes getCampusIes() {
		return this.campusIes;
	}

	public void setCampusIes(CampusIes campusIes) {
		this.campusIes = campusIes;
	}

	@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_endereco", unique = false, nullable = true, insertable = true, updatable = true)
	public Endereco getEndereco() {
		return this.endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	@Column(name = "sigla", unique = false, nullable = false, insertable = true, updatable = true, length = 20)
	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@Column(name = "nome", unique = false, nullable = false, insertable = true, updatable = true, length = 120)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_ies")
	public InstituicoesEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicoesEnsino instituicao) {
		this.instituicao = instituicao;
	}

	@Transient
	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(nome, "Nome", lista);
		ValidatorUtil.validateRequired(sigla, "Sigla", lista);
		ValidatorUtil.validateRequired(endereco.getCep(), "CEP", lista);
		ValidatorUtil.validateRequired(endereco.getLogradouro(), "Logradouro", lista);
		ValidatorUtil.validateRequired(endereco.getBairro(), "Bairro", lista);
		return lista;
	}

}
