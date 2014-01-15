/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.TipoEsferaAdministrativa;
import br.ufrn.sigaa.ensino.latosensu.dominio.ParceriaLato;

/**
 * PessoaJurídica do SIGAA, dados específicos das pessoa jurídicas no sistema acadêmico.
 * 
 * @author Jean Guerethes
 */
@Entity
@Table(schema="comum", name = "pessoa_juridica", uniqueConstraints = {})
public class PessoaJuridica implements PersistDB {

	// Fields

	private int id;

	private String razaoSocial;

	private Endereco endereco =  new Endereco();

	private TipoEsferaAdministrativa tipoEsferaAdministrativa= new TipoEsferaAdministrativa();

	private String nomeDirigente;

	private String cargoDirigente;

	private String esferaAdministrativaOutra;

	private boolean finsLucrativos;

	private Integer registroCnss;

	private Integer numDeclaracaoFederal;

	private Integer numDeclaracaoMunicipal;

	private Integer numDeclaracaoEstadual;

	private boolean privada;

	private String atividadeFim;

	private Pessoa pessoa = new Pessoa();

	private Set<ParceriaLato> parcerias = new HashSet<ParceriaLato>(0);

	// Constructors

	/** default constructor */
	public PessoaJuridica() {
	}

	/** minimal constructor */
	public PessoaJuridica(int idPessoaJuridica) {
		this.id = idPessoaJuridica;
	}

	/** full constructor */
	public PessoaJuridica(int idPessoaJuridica, Endereco endereco,
			TipoEsferaAdministrativa tipoEsferaAdministrativa,
			String nomeDirigente, String cargoDirigente,
			String esferaAdministrativaOutra, boolean finsLucrativos,
			Integer registroCnss, Integer numDeclaracaoFederal,
			Integer numDeclaracaoMunicipal, Integer numDeclaracaoEstadual,
			boolean privada, String atividadeFim, Pessoa pessoa,
			Set<ParceriaLato> parcerias) {
		this.id = idPessoaJuridica;
		this.endereco = endereco;
		this.tipoEsferaAdministrativa = tipoEsferaAdministrativa;
		this.nomeDirigente = nomeDirigente;
		this.cargoDirigente = cargoDirigente;
		this.esferaAdministrativaOutra = esferaAdministrativaOutra;
		this.finsLucrativos = finsLucrativos;
		this.registroCnss = registroCnss;
		this.numDeclaracaoFederal = numDeclaracaoFederal;
		this.numDeclaracaoMunicipal = numDeclaracaoMunicipal;
		this.numDeclaracaoEstadual = numDeclaracaoEstadual;
		this.privada = privada;
		this.atividadeFim = atividadeFim;
		this.pessoa = pessoa;
		this.parcerias = parcerias;
	}

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_pessoa_juridica", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idPessoaJuridica) {
		this.id = idPessoaJuridica;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_endereco", unique = false, nullable = true, insertable = true, updatable = true)
	public Endereco getEndereco() {
		return this.endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tipo_esfera_administrativa", unique = false, nullable = true, insertable = true, updatable = true)
	public TipoEsferaAdministrativa getTipoEsferaAdministrativa() {
		return this.tipoEsferaAdministrativa;
	}

	public void setTipoEsferaAdministrativa(
			TipoEsferaAdministrativa tipoEsferaAdministrativa) {
		this.tipoEsferaAdministrativa = tipoEsferaAdministrativa;
	}

	@Column(name = "nome_dirigente", unique = false, nullable = true, insertable = true, updatable = true, length = 100)
	public String getNomeDirigente() {
		return this.nomeDirigente;
	}

	public void setNomeDirigente(String nomeDirigente) {
		this.nomeDirigente = nomeDirigente;
	}

	@Column(name = "cargo_dirigente", unique = false, nullable = true, insertable = true, updatable = true, length = 100)
	public String getCargoDirigente() {
		return this.cargoDirigente;
	}

	public void setCargoDirigente(String cargoDirigente) {
		this.cargoDirigente = cargoDirigente;
	}

	@Column(name = "esfera_administrativa_outra", unique = false, nullable = true, insertable = true, updatable = true, length = 50)
	public String getEsferaAdministrativaOutra() {
		return this.esferaAdministrativaOutra;
	}

	public void setEsferaAdministrativaOutra(String esferaAdministrativaOutra) {
		this.esferaAdministrativaOutra = esferaAdministrativaOutra;
	}

	@Column(name = "fins_lucrativos", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isFinsLucrativos() {
		return this.finsLucrativos;
	}

	public void setFinsLucrativos(boolean finsLucrativos) {
		this.finsLucrativos = finsLucrativos;
	}

	@Column(name = "registro_cnss", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getRegistroCnss() {
		return this.registroCnss;
	}

	public void setRegistroCnss(Integer registroCnss) {
		this.registroCnss = registroCnss;
	}

	@Column(name = "num_declaracao_federal", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getNumDeclaracaoFederal() {
		return this.numDeclaracaoFederal;
	}

	public void setNumDeclaracaoFederal(Integer numDeclaracaoFederal) {
		this.numDeclaracaoFederal = numDeclaracaoFederal;
	}

	@Column(name = "num_declaracao_municipal", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getNumDeclaracaoMunicipal() {
		return this.numDeclaracaoMunicipal;
	}

	public void setNumDeclaracaoMunicipal(Integer numDeclaracaoMunicipal) {
		this.numDeclaracaoMunicipal = numDeclaracaoMunicipal;
	}

	@Column(name = "num_declaracao_estadual", unique = false, nullable = true, insertable = true, updatable = true)
	public Integer getNumDeclaracaoEstadual() {
		return this.numDeclaracaoEstadual;
	}

	public void setNumDeclaracaoEstadual(Integer numDeclaracaoEstadual) {
		this.numDeclaracaoEstadual = numDeclaracaoEstadual;
	}

	@Column(name = "privada", unique = false, nullable = true, insertable = true, updatable = true)
	public boolean isPrivada() {
		return this.privada;
	}

	public void setPrivada(boolean privada) {
		this.privada = privada;
	}

	@Column(name = "atividade_fim", unique = false, nullable = true, insertable = true, updatable = true, length = 100)
	public String getAtividadeFim() {
		return this.atividadeFim;
	}

	public void setAtividadeFim(String atividadeFim) {
		this.atividadeFim = atividadeFim;
	}

	@ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pessoa", unique = false, nullable = false, insertable = true, updatable = true)
	public Pessoa getPessoa() {
		return this.pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	@OneToMany(cascade = { }, fetch = FetchType.LAZY, mappedBy = "pessoaJuridica")
	public Set<ParceriaLato> getParcerias() {
		return this.parcerias;
	}

	public void setParcerias(Set<ParceriaLato> parcerias) {
		this.parcerias = parcerias;
	}

	@Column(name = "razao_social", unique = false, nullable = false, insertable = true, updatable = true)
	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	@Transient
	public String getNomeFantasia() {
		return this.pessoa.getNome();
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

}
