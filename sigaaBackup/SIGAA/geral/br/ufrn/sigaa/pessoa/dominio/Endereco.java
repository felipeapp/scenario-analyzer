/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '13/09/2006'
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.UnidadeFederativa;

/**
 * Entidade que armazena os dados de endereço 
 */
@Entity
@Table(schema="comum", name = "endereco", uniqueConstraints = {})
public class Endereco implements PersistDB {

	// Fields

	/** Chave primária. */
	private int id;

	/** referencia à unidade federativa do endereço */
	private UnidadeFederativa unidadeFederativa = new UnidadeFederativa();

	/** referencia ao município do endereço */
	private Municipio municipio = new Municipio();

	/** tipo de logradouro (av, rua, travessa, etc) */
	private TipoLogradouro tipoLogradouro = new TipoLogradouro();

	/** pais do endereço */
	private Pais pais;

	/** Nome da avenida, rua, praça, travessa, alameda, beco, passagem, etc. 
	 * Deve ser atualizado automaticamente pelo programa quando o CEP for preenchido e o campo logradouro da tabela CEP estiver preenchido. */
	private String logradouro;

	/** Número do prédio */
	private String numero;

	/** Complemento (bloco, apartamento, etc.) */
	private String complemento;

	/** cep */
	private String cep;

	/** Nome da cidade, município, distrito ou povoado. 
	 * Deve ser preenchido quando se trata de município fora do Brasil. 
	 * Deve ser atualizado automaticamente pelo programa quando o CEP for preenchido. */
	private String municipioOutro;

	/** Caixa Postal */
	private String caixaPostal;

	/** Nome do bairro */
	private String bairro;

	/** Deve ser preenchido somente se não constar na tabela Pais. */
	private String nomePais;

	/** Indica se o registro de endereço é o de correspondência da pessoa.
	S -Sim
	N - Não */
	private Boolean correspondencia;

	// Constructors

	@Override
	public String toString() {
		return getDescricao();
	}

	/** default constructor */
	public Endereco() {
	}

	/** default minimal constructor */
	public Endereco(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public Endereco(int idEndereco, String logradouro) {
		this.id = idEndereco;
		this.logradouro = logradouro;
	}

	/** full constructor */
	public Endereco(int idEndereco, UnidadeFederativa unidadeFederativa,
			TipoLogradouro tipoLogradouro, Pais pais, String logradouro,
			String numero, String complemento, String cep, String municipio,
			String caixaPostal, String bairro, String nomePais,
			boolean correspondencia) {
		this.id = idEndereco;
		this.unidadeFederativa = unidadeFederativa;
		this.tipoLogradouro = tipoLogradouro;
		this.pais = pais;
		this.logradouro = logradouro;
		this.numero = numero;
		this.complemento = complemento;
		this.cep = cep;
		this.municipioOutro = municipio;
		this.caixaPostal = caixaPostal;
		this.bairro = bairro;
		this.nomePais = nomePais;
		this.correspondencia = correspondencia;
	}

	// Property accessors
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_endereco", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idEndereco) {
		this.id = idEndereco;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade_federativa", unique = false, nullable = true, insertable = true, updatable = true)
	public UnidadeFederativa getUnidadeFederativa() {
		return this.unidadeFederativa;
	}

	public void setUnidadeFederativa(UnidadeFederativa unidadeFederativa) {
		this.unidadeFederativa = unidadeFederativa;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_municipio", unique = false, nullable = true, insertable = true, updatable = true)
	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo_logradouro", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoLogradouro getTipoLogradouro() {
		return this.tipoLogradouro;
	}

	public void setTipoLogradouro(TipoLogradouro tipoLogradouro) {
		this.tipoLogradouro = tipoLogradouro;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pais", unique = false, nullable = true, insertable = true, updatable = true)
	public Pais getPais() {
		return this.pais;
	}

	public void setPais(Pais pais) {
		this.pais = pais;
	}

	@Column(name = "logradouro", unique = false, nullable = true, insertable = true, updatable = true, length = 120)
	public String getLogradouro() {
		return this.logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	@Column(name = "numero", unique = false, nullable = true, insertable = true, updatable = true, length = 12)
	public String getNumero() {
		return this.numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	@Column(name = "complemento", unique = false, nullable = true, insertable = true, updatable = true, length = 20)
	public String getComplemento() {
		return this.complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	@Column(name = "cep", unique = false, nullable = true, insertable = true, updatable = true)
	public String getCep() {
		return this.cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	@Column(name = "municipio_outro", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	public String getMunicipioOutro() {
		return this.municipioOutro;
	}

	public void setMunicipioOutro(String municipio) {
		this.municipioOutro = municipio;
	}

	@Column(name = "caixa_postal", unique = false, nullable = true, insertable = true, updatable = true, length = 12)
	public String getCaixaPostal() {
		return this.caixaPostal;
	}

	public void setCaixaPostal(String caixaPostal) {
		this.caixaPostal = caixaPostal;
	}

	@Column(name = "bairro", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	public String getBairro() {
		return this.bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	@Column(name = "nome_pais", unique = false, nullable = true, insertable = true, updatable = true, length = 80)
	public String getNomePais() {
		return this.nomePais;
	}

	public void setNomePais(String nomePais) {
		this.nomePais = nomePais;
	}

	@Column(name = "correspondencia", unique = false, nullable = true, insertable = true, updatable = true)
	public Boolean isCorrespondencia() {
		return this.correspondencia;
	}

	public void setCorrespondencia(Boolean correspondencia) {
		this.correspondencia = correspondencia;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	/** Retorna uma descrição textual deste Endereço.
	 * @return
	 */
	@Transient
	public String getDescricao() {
		StringBuilder desc = new StringBuilder();
		
		if (tipoLogradouro != null && !ValidatorUtil.isEmpty(tipoLogradouro.getDescricao()))
			desc.append(tipoLogradouro + " ");
		
		desc.append(logradouro);
		if (numero != null) desc.append(", " + numero);
		if (!ValidatorUtil.isEmpty(complemento)) desc.append(" - " + complemento);
		return desc.toString();
	}
	
	@Transient
	public boolean isEmpty() {
		return ValidatorUtil.isEmpty(logradouro) && ValidatorUtil.isEmpty(cep) && ValidatorUtil.isEmpty(numero);
	}
}
