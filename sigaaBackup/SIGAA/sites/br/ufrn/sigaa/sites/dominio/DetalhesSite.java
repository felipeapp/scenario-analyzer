/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/11/2008
 *
 */
package br.ufrn.sigaa.sites.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Classe de Domínio que representa os portais públicos do SIGAA (Centro, Departamento, Cursos).
 * Contem detalhes como texto de apresentação, logo, endereço de acesso.
 * 
 * @author Victor Hugo
 */
@Entity
@Table(name = "detalhes_site", schema = "site")
public class DetalhesSite implements Validatable {

	/** Constantes que definem as dimensões das imagens da logo e apresentação */
	public static final int LOGO_WIDTH = 200;
	public static final int LOGO_HEIGHT = 110;

	public static final int FOTO_WIDTH = 625;
	public static final int FOTO_HEIGHT = 180;

	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_detalhes_site", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Atribui o site a um template css. */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="id_template_site")
	private TemplateSite templateSite;
	
	/** Atribui o site a uma unidade quando essa for um centro, departamento ou programa. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_unidade", unique = false, nullable = true, insertable = true, updatable = true)
	private Unidade unidade;
	
	/** Atribui o site a um curso quando esse for graduação, lato-sensu e técnico. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso", unique = false, nullable = true, insertable = true, updatable = true)
	private Curso curso;

	/** Identificador da imagem da logo	 */
	@Column(name = "id_logo")
	private Integer idLogo;

	/** Identificador da imagem da apresentação	 */
	@Column(name = "id_foto")
	private Integer idFoto;

	/** Atribui uma sigla ou apelido que identifique o portal no endereço eletrônico. */
	private String url;
	
	/** Atribui um endereço eletrônico ao portal quando este possuir um site alternativo. */
	@Column(name = "site_extra")
	private String siteExtra;

	/** Atribui um texto introdutório ao portal. */
	@Column(name = "apresentacao")
	private String introducao;

	/** Atribui um texto introdutório no idioma inglês ao portal. */
	@Column(name = "apresentacao_en")
	private String introducaoEN;

	/** Atribui um texto introdutório no idioma francês ao portal. */
	@Column(name = "apresentacao_fr")
	private String introducaoFR;

	/** Atribui um texto introdutório no idioma espanhol ao portal. */
	@Column(name = "apresentacao_es")
	private String introducaoES;
	
	/** Atribui um endereço eletrônico quando este portal for de um programa e possuir um site obrigatório. */
	@Column(name = "site_proprio_obrigatorio")
	private Boolean siteProprioObrigatorio;

	/** Atribui um registro que identifica quem e quando foi cadastrado. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Atribui a data em que o portal foi cadastrado. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@CriadoEm
	private Date dataCadastro;

	/** Atribui um novo registro que identifica quem e quando foi atualizado. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao", unique = false, nullable = true, insertable = true, updatable = true)
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Atribui a data em que o portal foi atualizado. */	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@AtualizadoEm
	private Date dataAtualizacao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getIdLogo() {
		return idLogo;
	}

	public void setIdLogo(Integer idLogo) {
		this.idLogo = idLogo;
	}

	public Integer getIdFoto() {
		return idFoto;
	}

	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getRegistroAtualizacao() {
		return registroAtualizacao;
	}

	public void setRegistroAtualizacao(RegistroEntrada registroAtualizacao) {
		this.registroAtualizacao = registroAtualizacao;
	}

	public Date getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(Date dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}
	
	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public String getIntroducao() {
		return introducao;
	}
	
	public void setIntroducao(String introducao) {
		this.introducao = introducao;
	}
	
	public String getIntroducaoEN() {
		return introducaoEN;
	}
	
	public void setIntroducaoEN(String introducaoEN) {
		this.introducaoEN = introducaoEN;
	}
	
	public String getIntroducaoFR() {
		return introducaoFR;
	}
	
	public void setIntroducaoFR(String introducaoFR) {
		this.introducaoFR = introducaoFR;
	}
	
	public String getIntroducaoES() {
		return introducaoES;
	}
	
	public void setIntroducaoES(String introducaoES) {
		this.introducaoES = introducaoES;
	}
	
	public String getSiteExtra() {
		return ( !isEmpty(siteExtra) && siteExtra.indexOf("http")==-1 )?"http://"+siteExtra:siteExtra;
	}
	
	public void setSiteExtra(String siteExtra) {
		this.siteExtra = siteExtra.indexOf("http")==-1?"http://"+siteExtra:siteExtra;
	}
	
	public Boolean getSiteProprioObrigatorio() {
		return (siteProprioObrigatorio==null || !siteProprioObrigatorio)?false:true;
	}
	
	public void setSiteProprioObrigatorio(Boolean siteProprioObrigatorio) {
		this.siteProprioObrigatorio = siteProprioObrigatorio;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		
		return lista;
	}

	public TemplateSite getTemplateSite() {
		return templateSite;
	}

	public void setTemplateSite(TemplateSite templateSite) {
		this.templateSite = templateSite;
	}

	@Transient
	public boolean isTipoUnidade() {
		return unidade != null;
	}

	@Transient
	public boolean isTipoCurso() {
		return curso != null;
	}
	
}
