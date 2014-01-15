/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Pais;

/**
 * Entidade que armazena os dados das financiadora de projetos
 */
@Entity
@Table(name = "entidade_financiadora", schema = "projetos")
public class EntidadeFinanciadora implements Validatable {

	// Fields

	private int id;
	
	/**
	 * Ao remover as produções as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 */
	private Boolean ativo = true;

	/** Tipo de classificação da entidade financiadora */
	private ClassificacaoFinanciadora classificacaoFinanciadora = new ClassificacaoFinanciadora();

	/** Unidade federativa da entidade financiadora */
	private UnidadeFederativa unidadeFederativa = new UnidadeFederativa();
	
	/** País da unidade financiadora */
	private Pais pais = new Pais();

	/** Nome da entidade financiadora */
	private String nome;

	/** Sigla da entidade financiadora */
	private String sigla;

	/** Grupo da entidade financiadora */
	private GrupoEntidadeFinanciadora grupoEntidadeFinanciadora = new GrupoEntidadeFinanciadora();

	// Constructors

	/** Construtor padrão */
	public EntidadeFinanciadora() {
	}

	/** default minimal constructor */
	public EntidadeFinanciadora(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public EntidadeFinanciadora(int idEntidadeFinanciadora, String nome) {
		this.id = idEntidadeFinanciadora;
		this.nome = nome;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_entidade_financiadora")           
	public int getId() {
		return this.id;
	}

	public void setId(int idEntidadeFinanciadora) {
		this.id = idEntidadeFinanciadora;
	}
	
	
	/**
	 * Ao remover as produções as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	@Column(name = "ativo")
	public Boolean getAtivo() {	return this.ativo; }

	/**
	 * Ao remover as produções as mesmas não serão removidas da base de dados,
	 * apenas o campo ativo será marcado como FALSE
	 * @author Edson Anibal (ambar@info.ufrn.br)
	 */
	public void setAtivo(Boolean ativo)	{ this.ativo = ativo; }

	

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_classificacao_financiadora", unique = false, nullable = true, insertable = true, updatable = true)
	public ClassificacaoFinanciadora getClassificacaoFinanciadora() {
		return this.classificacaoFinanciadora;
	}

	public void setClassificacaoFinanciadora(
			ClassificacaoFinanciadora classificacaoFinanciadora) {
		this.classificacaoFinanciadora = classificacaoFinanciadora;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade_federativa", unique = false, nullable = true, insertable = true, updatable = true)
	public UnidadeFederativa getUnidadeFederativa() {
		return this.unidadeFederativa;
	}

	public void setUnidadeFederativa(UnidadeFederativa unidadeFederativa) {
		this.unidadeFederativa = unidadeFederativa;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pais", unique = false, nullable = true, insertable = true, updatable = true)
	public Pais getPais() {
		return this.pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	@Column(name = "nome", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "sigla", unique = false, nullable = true, insertable = true, updatable = true, length = 20)
	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_agrupamento_ent_financ", unique = false, nullable = true, insertable = true, updatable = true)
	public GrupoEntidadeFinanciadora getGrupoEntidadeFinanciadora() {
		return grupoEntidadeFinanciadora;
	}

	public void setGrupoEntidadeFinanciadora(
			GrupoEntidadeFinanciadora grupoEntidadeFinanciadora) {
		this.grupoEntidadeFinanciadora = grupoEntidadeFinanciadora;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj,"nome","sigla");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll( nome, sigla );
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(sigla, "Sigla da Entidade", lista);
		ValidatorUtil.validateRequired(nome, "Nome da Unidade", lista);
		ValidatorUtil.validateRequired(pais, "País", lista);
		ValidatorUtil.validateRequired(classificacaoFinanciadora, "Classificação da Entidade", lista);
		ValidatorUtil.validateRequired(grupoEntidadeFinanciadora, "Grupo da Entidade Financiadora", lista);
		if ( unidadeFederativa.getId() == 0 ) {
			unidadeFederativa = null;
		}
		return lista;
	}

	@Override
	public String toString() {
		return sigla + " - " + nome;
	}
}
