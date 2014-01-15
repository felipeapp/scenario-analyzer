/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '13/09/2006'
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.UnidadeFederativa;

/**
 * Entidade que armazena os dados dos municípios
 */
@Entity
@Table(schema="comum", name = "municipio", uniqueConstraints = {})
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Municipio implements Validatable, Comparable<Municipio> {

	/** Id da UF padrão que deve ser utilizado pelos sistemas. */
	public static final int ID_MUNICIPIO_PADRAO = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.MUNICIPIO_PADRAO);
	
	// Fields

	/** Chave primária. */
	private int id;

	/** unidade federativo ao qual o município pertence */
	private UnidadeFederativa unidadeFederativa = new UnidadeFederativa();

	/** código do município */
	private String codigo;

	/** nome do município */
	private String nome;
	
	/** nome do município sem acentos */
	private String nomeAscii;

	/** diz se o município está ativo ou não */
	private boolean ativo;
	// Constructors

	/** Indica se o município é ativo no banco.
	 * @return the ativo
	 */
	public boolean isAtivo() {
		return ativo;
	}

	/** Seta se o município é ativo no banco.
	 * @param ativo the ativo to set
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	/** default constructor */
	public Municipio() {
	}

	/** default minimal constructor */
	public Municipio(int id) {
		this.id = id;
		unidadeFederativa = null;
	}
	
	/** default minimal constructor */
	public Municipio(String nome) {
		this.nome = nome;
	}
	
	public Municipio(int id, String nome) {
		this(id);
		this.nome = nome;
	}

	/** minimal constructor */
	public Municipio(int idMunicipio, UnidadeFederativa unidadeFederativa,
			String codigo, String nome) {
		this.id = idMunicipio;
		this.unidadeFederativa = unidadeFederativa;
		this.codigo = codigo;
		this.nome = nome;
	}

	// Property accessors
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="comum.municipio_sequence") })
	@Column(name = "id_municipio", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idMunicipio) {
		this.id = idMunicipio;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade_federativa", unique = false, nullable = false, insertable = true, updatable = true)
	public UnidadeFederativa getUnidadeFederativa() {
		return this.unidadeFederativa;
	}

	public void setUnidadeFederativa(UnidadeFederativa unidadeFederativa) {
		this.unidadeFederativa = unidadeFederativa;
	}

	@Column(name = "codigo", unique = false, nullable = false, insertable = true, updatable = true, length = 12)
	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	@Column(name = "nome", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
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
	public String toString() {
		StringBuilder str = new StringBuilder(nome);
		if (!isEmpty(unidadeFederativa))
				str.append(" - ").append(unidadeFederativa.getSigla()); 
		return str.toString();
	}

	/** Retorna o nome da Unidade Federativa
	 * @return
	 */
	@Transient
	public String getNomeUF() {
		StringBuilder sb = new StringBuilder();
		if (nome != null)
			sb.append(nome);
		if (unidadeFederativa != null)
			sb.append(" - " + unidadeFederativa.getSigla());
		return sb.toString();
	}

	/** Valida os dados do município.
	 * @see br.ufrn.arq.negocio.validacao.Validatable#validate()
	 */
	public ListaMensagens validate() {
		ListaMensagens erros = new ListaMensagens();
		ValidatorUtil.validateRequiredId(unidadeFederativa.getId(), "Unidade Federativa", erros);
		ValidatorUtil.validateRequired(codigo, "Código", erros);
		ValidatorUtil.validateRequired(nome, "Nome", erros);
		return erros;
	}

	/** Compara este município com o especificado, para fins de ordenação.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Municipio m) {
		return new CompareToBuilder()
			.append(this.getNome(), m.getNome())
			.toComparison();
	}

	@Column(name = "nome_ascii")
	public String getNomeAscii() {
		return nomeAscii;
	}

	public void setNomeAscii(String nomeAscii) {
		this.nomeAscii = nomeAscii;
	}

}
