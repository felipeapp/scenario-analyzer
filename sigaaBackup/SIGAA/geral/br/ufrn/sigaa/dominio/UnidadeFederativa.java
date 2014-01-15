/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '13/09/2006'
 *
 */
package br.ufrn.sigaa.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pais;

/**
 * Representa uma Unidade Federativa. Ex. RN, SP, DF.
 *
 */
@Entity
@Table(schema = "comum", name = "unidade_federativa", uniqueConstraints = {})
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class UnidadeFederativa implements Validatable {

	/**
	 * Id da UF padrão que deve ser utilizado pelos sistemas. 
	 */
	public static final int ID_UF_PADRAO = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.UF_PADRAO);
	
	// Fields

	/** Chave primária. */
	private int id;

	/** País ao qual esta unidade federativa pertence. */
	private Pais pais = new Pais();

	/** Nome da unidade federativa. Ex.: Rio Grande do Norte, São Paulo, etc. */
	private String descricao;

	/** Sigla da unidade federativa. Ex.: RN, SP, etc. */
	private String sigla;

	/** Município que é a capital desta unidade federativa. */
	private Municipio capital;
	
	/** Artigo genitivo que define a relação de posse com a Unidade Federativa. 
	 * Ex.: ... natural do Rio Grande do Norte, ... natural de São Paulo, etc.
	 * O artigo genitivo, nestes exemplos, são 'O' e 'A' respectivamente. */  
	private String artigoGenitivo;

	/** Contrutor padrão */
	public UnidadeFederativa() {
	}

	/** default minimal constructor */
	public UnidadeFederativa(int id) {
		this.id = id;
	}


	/** minimal constructor */
	public UnidadeFederativa(int idUnidadeFederativa, String sigla) {
		this.id = idUnidadeFederativa;
		this.sigla = sigla;
	}



	// Property accessors
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_unidade_federativa", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idUnidadeFederativa) {
		this.id = idUnidadeFederativa;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pais", unique = false, nullable = true, insertable = true, updatable = true)
	public Pais getPais() {
		return this.pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true, length = 128)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "sigla", unique = false, nullable = false, insertable = true, updatable = true, length = 2)
	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@ManyToOne
	@JoinColumn(name="id_capital")
	public Municipio getCapital() {
		return capital;
	}

	public void setCapital(Municipio capital) {
		this.capital = capital;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(descricao, "Descricao", lista);
		ValidatorUtil.validateRequired(sigla, "Sigla", lista);
		return lista;
	}

	@Column(name = "artigo_genitivo")
	public String getArtigoGenitivo() {
		return artigoGenitivo;
	}

	public void setArtigoGenitivo(String artigoGenitivo) {
		this.artigoGenitivo = artigoGenitivo;
	}

	/** Retorna uma representação textual deste objeto no formato: sigla da UF
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return sigla;
	}
}