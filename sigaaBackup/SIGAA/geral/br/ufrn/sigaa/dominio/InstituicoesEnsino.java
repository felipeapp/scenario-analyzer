/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '01/12/2006'
 *
 */
package br.ufrn.sigaa.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.Pais;

/**
 * Entidade que armazena as instituições de ensino
 */
@Entity
@Table(schema = "comum", name = "instituicoes_ensino", uniqueConstraints = {})
public class InstituicoesEnsino implements Validatable {

	@Transient
	public static final int UFRN = ParametroHelper.getInstance().getParametroInt(ParametrosGerais.IDENTIFICADOR_INSTITUICAO_UFRN);
	
	/**
	 * Código da UFRN no sistema CAPES
	 */
	public static final int COD_CAPES_UFRN = ParametroHelper.getInstance().getParametroInt(ParametrosGerais.COD_CAPES_UFRN);

	/** Nome da UFRN */
	public static final String NOME_UFRN = RepositorioDadosInstitucionais.get("nomeInstituicao");
	
	// Fields

	private int id;

	/** Nome da instituição de ensino */
	private String nome;

	/** Sigla da instituição */
	private String sigla;

	/** CNPJ da instituição de ensino */
	private Long cnpj;

	/** Diz se é uma instituição estrangeira ou nacional */
	private boolean estrangeira;

	/** Tipo de instituição*/
	private TipoInstituicao tipo = new TipoInstituicao( TipoInstituicao.TIPO_ENSINO );

	/** País da instituição, se for uma instituição estrangeira o pais NÃO deve ser Brasil */
	private Pais pais; // Pais
	
	/** Associação de curso a instituições de ensino. */
	private Set<CursoInstituicoesEnsino> cursoInstituicoesEnsino = new HashSet<CursoInstituicoesEnsino>(0);

	// Constructors

	public InstituicoesEnsino() {
	}

	public InstituicoesEnsino(int id) {
		this.id = id;
	}

	public InstituicoesEnsino(int id, String n, String si) {
		this.id = id;
		nome = n;
		sigla = si;
	}

	public InstituicoesEnsino(int id, String nome, boolean estrangeira) {
		this.id = id;
		this.nome = nome;
		this.estrangeira = estrangeira;
	}

	public InstituicoesEnsino(int id, String nome, String sigla,
			boolean estrangeira, long cnpj, Pais pais) {
		this.id = id;
		this.nome = nome;
		this.sigla = sigla;
		this.estrangeira = estrangeira;
		this.cnpj = cnpj;
		this.pais = pais;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "nome", unique = false, nullable = false, insertable = true, updatable = true, length = 120)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Column(name = "sigla", unique = false, nullable = true, insertable = true, updatable = true, length = 12)
	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@Column(name = "estrangeira", unique = false, nullable = false, insertable = true, updatable = true)
	public boolean isEstrangeira() {
		return this.estrangeira;
	}

	public void setEstrangeira(boolean estrangeira) {
		this.estrangeira = estrangeira;
	}

	@Column(name = "cnpj", unique = false, nullable = true, insertable = true, updatable = true)
	public Long getCnpj() {
		return this.cnpj;
	}

	public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pais")
	public Pais getPais() {
		return this.pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	@Transient
	public Integer getIdObject() {
		return id;
	}

	@Transient
	public String getDescricao() {
		return getSigla() + " - " + getNome();
	}

	public void setIdObject(Integer idObject) {
		if (idObject != null) {
			id = idObject;
		}
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/**
	 * Valida os campos obrigatórios para persistir a entidade
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(nome, "Nome", lista);
		ValidatorUtil.validateRequired(sigla, "Sigla", lista);
		return lista;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo")
	public TipoInstituicao getTipo() {
		return tipo;
	}

	public void setTipo(TipoInstituicao tipo) {
		this.tipo = tipo;
	}

	@Transient
	public String getNomeSigla() {
	    return nome + " - " + sigla;
	}

	@OneToMany(cascade = {}, fetch = FetchType.EAGER, mappedBy = "instituicaoEnsino")
	public Set<CursoInstituicoesEnsino> getCursoInstituicoesEnsino() {
		return cursoInstituicoesEnsino;
	}

	public void setCursoInstituicoesEnsino(
			Set<CursoInstituicoesEnsino> cursoInstituicoesEnsino) {
		this.cursoInstituicoesEnsino = cursoInstituicoesEnsino;
	}
}
