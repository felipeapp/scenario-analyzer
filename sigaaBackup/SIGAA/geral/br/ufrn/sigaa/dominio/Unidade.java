/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '13/09/2006'
 *
 */
package br.ufrn.sigaa.dominio;

// Generated 13/09/2006 08:49:22 by Hibernate Tools 3.1.0.beta5

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.comum.dominio.AmbienteOrganizacionalUnidade;
import br.ufrn.comum.dominio.AreaAtuacaoUnidade;
import br.ufrn.comum.dominio.TipoUnidadeOrganizacional;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.sites.dominio.DetalhesSite;

/**
 * Unidade do SIGAA
 */
@Entity
@Table(schema="comum", name = "unidade", uniqueConstraints = { @UniqueConstraint(columnNames = { "codigo_unidade" }) })
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Unidade extends UnidadeGeral implements PersistDB, Comparable<Unidade> {

	// Fields
	private Municipio municipio;
	
	private DetalhesSite detalhesSite;

	/**
	 * Collection usada para utilizar em qualquer relatório que necessite de agrupar
	 * por unidade;
	 */
	private Collection colectionGeral;

	/** default constructor */
	public Unidade() {
	}
	
	/**
	 * Construtor parametrizado
	 * 
	 * @param unidadeGeral
	 */
	public Unidade(UnidadeGeral unidadeGeral) {
		this(unidadeGeral.getId());
	}

	public Unidade(int id, Long codigo, String nome, String sigla) {
		super(id,codigo,nome,sigla);
	}

	public Unidade( int id, Long codigo, String nome, String sigla, String nomeCapa, String hierarquia){
		super(id,codigo,nome,sigla,nomeCapa,hierarquia);
		setId(id);
	}

	public Unidade( int id, Long codigo, String nome, String sigla, String nomeCapa, String hierarquia, String municipio){
		super(id,codigo,nome,sigla,nomeCapa,hierarquia);
		setId(id);
		this.municipio = new Municipio();
		this.municipio.setNome(municipio);
	}

	/** Construtor parametrizado.
	 * @param id
	 * @param codigo
	 * @param nome
	 * @param sigla
	 * @param nomeCapa
	 * @param hierarquia
	 * @param municipio
	 * @param siglaAcademica
	 * @param tipoAcademica
	 */
	public Unidade( int id, Long codigo, String nome, String sigla, String nomeCapa, String hierarquia, String municipio, String siglaAcademica, Integer tipoAcademica){
		super(id,codigo,nome,sigla,nomeCapa,hierarquia);
		setId(id);
		this.municipio = new Municipio();
		this.municipio.setNome(municipio);
		this.setSiglaAcademica(siglaAcademica);
		this.tipoAcademica = tipoAcademica;
	}

	/** Construtor parametrizado.
	 * @param id
	 */
	public Unidade(int id) {
		this.id = id;
	}

	/** minimal constructor */
	public Unidade(int idUnidade, Unidade unidadeByIdGestora, int categoria, boolean sipac) {
		this.id = idUnidade;
		setGestora(unidadeByIdGestora);
		this.categoria = categoria;
		this.unidadeSipac = sipac;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_unidade", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return id;
	}

	@Override
	@Column(name = "codigo_unidade")
	public Long getCodigo() {
		return codigo;
	}

	@Override
	@Column(name = "nome")
	public String getNome() {
		return nome;
	}
	
	@Override
	@Column(name = "nome_ascii")
	public String getNomeAscii() {
		return nomeAscii;
	}

	@Override
	@Column(name = "nome_capa")
	public String getNomeCapa() {
		return nomeCapa;
	}

	/** Retorna a sigla da unidade. 
	 * @see br.ufrn.comum.dominio.UnidadeGeral#getSigla()
	 */
	@Override
	@Column(name = "sigla")
	public String getSigla() {
		return sigla;
	}

	/** Indica se a Unidade é de DIREITO ou DE FATO. 
	 * @see br.ufrn.comum.dominio.UnidadeGeral#getTipo()
	 */
	@Override
	@Column(name = "tipo")
	public int getTipo() {
		return tipo;
	}
	@Override
	@Column(name = "categoria")
	public int getCategoria() {
		return categoria;
	}
	@Override
	@Column(name = "hierarquia")
	public String getHierarquia() {
		return hierarquia;
	}

	@Override
	@Column(name = "cnpj")
	public Long getCnpj() {
		return cnpj;
	}
	
	@Override
	@ManyToOne
	@JoinColumn(name="unidade_responsavel")
	public Unidade getUnidadeResponsavel() {
		return (Unidade) unidadeResponsavel;
	}

	public void setUnidadeResponsavel(Unidade unidadeResponsavel){
		this.unidadeResponsavel = unidadeResponsavel;
	}

	@Override
	@ManyToOne
	@JoinColumn(name = "id_gestora", unique = false, nullable = false, insertable = true, updatable = true)
	public Unidade getGestora() {
		return (Unidade)gestora;
	}

	public void setGestora(Unidade gestora){
		this.gestora = gestora;
	}

	@Override
	@Column(name = "UNIDADE_ORCAMENTARIA")
	public boolean isUnidadeOrcamentaria() {
		return unidadeOrcamentaria;
	}


	@Override
	@Column(name = "sipac")
	public boolean isUnidadeSipac() {
		return unidadeSipac;
	}

	@Override
	@Column(name = "unidade_academica")
	public boolean isUnidadeAcademica() {
		return unidadeAcademica;
	}

	@Override
	@Column(name = "organizacional")
	public boolean isOrganizacional() {
		return organizacional;
	}


	@Override
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unid_resp_org")
	public Unidade getResponsavelOrganizacional() {
		return (Unidade)responsavelOrganizacional;
	}


	public void setResponsavelOrganizacional(Unidade responsavelOrganizacional){
		this.responsavelOrganizacional = responsavelOrganizacional;
	}

	@Override
	@Column(name = "hierarquia_organizacional")
	public String getHierarquiaOrganizacional() {
		return hierarquiaOrganizacional;
	}

	@Override
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_tipo_organizacional")
	public TipoUnidadeOrganizacional getTipoOrganizacional() {
		return tipoOrganizacional;
	}

	@Override
	@Column(name = "codigo_siapecad")
	public Long getCodigoSiapecad() {
		return codigoSiapecad;
	}

	@Override
	@Column(name = "email")
	public String getEmail() {
		return email;
	}

	/** Retorna o telefone oficial da unidade 
	 * @see br.ufrn.comum.dominio.UnidadeGeral#getTelefone()
	 */
	@Override
	@Column(name = "telefones")
	public String getTelefone() {
		return telefone;
	}

	/** Retorna o endereço oficial da unidade 
	 * @see br.ufrn.comum.dominio.UnidadeGeral#getEndereco()
	 */
	@Override
	@Column(name = "endereco")
	public String getEndereco() {
		return endereco;
	}
	
	/** Retorna a UF da unidade 
	 * @see br.ufrn.comum.dominio.UnidadeGeral#getUf()
	 */
	@Override
	@Column(name = "uf")
	public String getUf() {
		return uf;
	}
	
	/** Retorna o CEP oficial da unidade.
	 * @see br.ufrn.comum.dominio.UnidadeGeral#getCep()
	 */
	@Override
	@Column(name = "cep")
	public String getCep() {
		return cep;
	}
	
	/** Retorna a data de criação da unidade. Documento legal de criação 
	 * @see br.ufrn.comum.dominio.UnidadeGeral#getDataCriacao()
	 */
	@Override
	@Temporal(TemporalType.DATE)
	@Column(name = "data_criacao")
	public Date getDataCriacao() {
		return dataCriacao;
	}

	/** Retorna a data de extinção da unidade. Documento legal de extinção 
	 * @see br.ufrn.comum.dominio.UnidadeGeral#getDataExtincao()
	 */
	@Override
	@Temporal(TemporalType.DATE)
	@Column(name = "data_extincao")
	public Date getDataExtincao() {
		return dataExtincao;
	}

	/** Retorna a área de atuação da unidade: meio, fim,...
	 * @see br.ufrn.comum.dominio.UnidadeGeral#getAreaAtuacao()
	 */
	@Override
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_area_atuacao")
	public AreaAtuacaoUnidade getAreaAtuacao() {
		return areaAtuacao;
	}

	/** Retorna o ambiente organizacional. Administrativo, saúde, informação, etc
	 * @see br.ufrn.comum.dominio.UnidadeGeral#getAmbienteOrganizacional()
	 */
	@Override
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name = "id_ambiente_organizacional")
	public AmbienteOrganizacionalUnidade getAmbienteOrganizacional() {
		return ambienteOrganizacional;
	}

	/** Indica se a unidade é de avaliação ou não 
	 * @see br.ufrn.comum.dominio.UnidadeGeral#isAvaliacao()
	 */
	@Override
	@Column(name = "avaliacao")
	public boolean isAvaliacao() {
		return avaliacao;
	}

	/** Indica se a unidade tem função remunerada 
	 * @see br.ufrn.comum.dominio.UnidadeGeral#isFuncaoRemunerada()
	 */
	@Override
	@Column(name = "funcao_remunerada")
	public boolean isFuncaoRemunerada() {
		return funcaoRemunerada;
	}

	/** Indica se a unidade é gestora de frequência 
	 * @see br.ufrn.comum.dominio.UnidadeGeral#isGestoraFrequencia()
	 */
	@Override
	@Column(name = "gestora_frequencia")
	public boolean isGestoraFrequencia() {
		return gestoraFrequencia;
	}


	/** Retona o tipo da função remunerada. (CD/FG) 
	 * @see br.ufrn.comum.dominio.UnidadeGeral#getTipoFuncaoRemunerada()
	 */
	@Override
	@Column(name = "tipo_funcao_remunerada")
	public int getTipoFuncaoRemunerada() {
		return tipoFuncaoRemunerada;
	}

	/** Seta o tipo da função remunerada. (CD/FG) 
	 * @see br.ufrn.comum.dominio.UnidadeGeral#setTipoFuncaoRemunerada(int)
	 */
	@Override
	public void setTipoFuncaoRemunerada(int tipoFuncaoRemunerada) {
		this.tipoFuncaoRemunerada = tipoFuncaoRemunerada;
	}


	/** Retorna o tipo de unidade acadêmica (Departamento, centro, PPG)
	 * @see br.ufrn.comum.dominio.UnidadeGeral#getTipoAcademica()
	 */
	@Override
	@Column(name = "tipo_academica")
	public Integer getTipoAcademica() {
		return tipoAcademica;
	}

	/** Seta o tipo de unidade acadêmica (Departamento, centro, PPG)
	 * @see br.ufrn.comum.dominio.UnidadeGeral#setTipoAcademica(java.lang.Integer)
	 */
	@Override
	public void setTipoAcademica(Integer tipoAcademica) {
		this.tipoAcademica = tipoAcademica;
	}

	/** Retorna a sigla utilizada, caso seja uma unidade acadêmica
	 * @see br.ufrn.comum.dominio.UnidadeGeral#getSiglaAcademica()
	 */
	@Override
	@Column(name = "sigla_academica")
	public String getSiglaAcademica() {
		return siglaAcademica;
	}

	/** Seta a sigla utilizada, caso seja uma unidade acadêmica
	 * @see br.ufrn.comum.dominio.UnidadeGeral#setSiglaAcademica(java.lang.String)
	 */
	@Override
	public void setSiglaAcademica(String siglaAcademica) {
		this.siglaAcademica = siglaAcademica;
	}

	/** Retorna, caso a unidade seja acadêmica, a sua gestora será a unidade que rege os cursos que ela oferece.
	 * @see br.ufrn.comum.dominio.UnidadeGeral#getGestoraAcademica()
	 */
	@Override
	@ManyToOne
	@JoinColumn(name = "id_gestora_academica")
	public Unidade getGestoraAcademica() {
		return (Unidade)gestoraAcademica;
	}

	/**
	 * Wrapper para que seja possível setar um objeto do tipo UnidadeGeral em
	 * gestoraAcademica como uma instância da classe Unidade.<br/>
	 * Não fazendo isto, ao tentar recuperar a gestoraAcademica através do
	 * método {@link #getGestoraAcademica()} gerará uma ClassCastException.
	 * 
	 * @see br.ufrn.comum.dominio.UnidadeGeral#setGestoraAcademica(br.ufrn.comum.dominio.UnidadeGeral)
	 */
	@Override
	public void setGestoraAcademica(UnidadeGeral gestoraAcademica) {
		if (gestoraAcademica instanceof Unidade) {
			this.gestoraAcademica = gestoraAcademica;
		} else if (gestoraAcademica != null){
			this.gestoraAcademica = new Unidade(gestoraAcademica.getId(), 
					gestoraAcademica.getCodigo(),
					gestoraAcademica.getNome(),
					gestoraAcademica.getSigla());
		} else {
			gestoraAcademica = null;
		}
	}
	
	/** Seta, caso a unidade seja acadêmica, a sua gestora será a unidade que rege os cursos que ela oferece.
	 * @param gestoraAcademica
	 */
	public void setGestoraAcademica(Unidade gestoraAcademica) {
		this.gestoraAcademica = gestoraAcademica;
	}


	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name="id_municipio")
	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	@Override
	@Transient
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	@Transient
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	@Transient
	public String getTipoAcademicaDesc() {
		if ( tipoAcademica != null ) {
			return TipoUnidadeAcademica.getDescricao(tipoAcademica);
		} else {
			return "NAO DEFINIDA";
		}
	}

	@Transient
	public String getNomeMunicipio() {
		return nome + ((municipio == null || municipio.getNome() == null) ?"":" - " + municipio.getNome()) ;
	}

	@Override
	@Column(name="submete_proposta_extensao")
	public Boolean getSubmetePropostaExtensao() {
		return submetePropostaExtensao;
	}

	@Override
	public void setSubmetePropostaExtensao(Boolean submetePropostaExtensao) {
		this.submetePropostaExtensao = submetePropostaExtensao;
	}

	@Transient
	public Collection getColectionGeral() {
		return colectionGeral;
	}

	public void setColectionGeral(Collection colectionGeral) {
		this.colectionGeral = colectionGeral;
	}

	@Transient
	public boolean isPrograma(){
		return tipoAcademica != null && tipoAcademica == TipoUnidadeAcademica.PROGRAMA_POS;
	}
	
	@Transient
	public boolean isProgramaResidencia(){
		return tipoAcademica != null && tipoAcademica == TipoUnidadeAcademica.PROGRAMA_RESIDENCIA;
	}

	@Transient
	public boolean isDepartamento(){
		return tipoAcademica != null && tipoAcademica == TipoUnidadeAcademica.DEPARTAMENTO;
	}
	
	@Transient
	public boolean isUnidadeAcademicaEspecializada(){
		return tipoAcademica != null && tipoAcademica == TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA;
	}

	@Transient
	public boolean isCentro(){
		return tipoAcademica != null && tipoAcademica == TipoUnidadeAcademica.CENTRO;
	}
	
	@Transient
	public DetalhesSite getDetalhesSite() {
		return detalhesSite;
	}

	@Transient	
	public void setDetalhesSite(DetalhesSite detalhesSite) {
		this.detalhesSite = detalhesSite;
	}

	public int compareTo(Unidade u) {
		return (this.nomeCapa.compareToIgnoreCase((u.getNomeCapa())));
	}

	
	@Override
	@Column(name="protocolizadora")
	public boolean isProtocolizadora() {
		return protocolizadora;
	}
	
	@Override
	@Column(name="radical")
	public Integer getRadical() {
		return radical;
	}	

	
	/** Indica se a unidade está ativa para ser associada a outros dados de outras tabelas 
	 * @see br.ufrn.comum.dominio.UnidadeGeral#isAtivo()
	 */
	@Override
	@Column(name="ativo")
	public boolean isAtivo() {
		return ativo;
	}
	
	@Override
	@Column(name="codigo_unidade_gestora_siafi")
	public Integer getCodigoUnidadeGestoraSIAFI() {
		return super.getCodigoUnidadeGestoraSIAFI();
	}	
	
}