/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/11/2008
 *
 */
package br.ufrn.sigaa.sites.dominio;

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

import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.AtualizadoEm;
import br.ufrn.arq.seguranca.log.AtualizadoPor;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Classe de domínio que representa seções extras a serem publicadas no portal público do SIGAA (Centros, Departamento, Programas e Cursos) 
 
 * @author Mario Rizzi Rocha
 * 
 */
@Entity
@Table(name = "secao_extra_site", schema = "site")
public class SecaoExtraSite implements Validatable {

	public static final int TAMANHO_MAXIMO = 300;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private int id;

	/** Atribui o id do centro, departamento ou programa ao qual esta seção pertence */
	@ManyToOne
	@JoinColumn(name = "id_unidade", nullable = true)
	private Unidade unidade;
	
	/** Atribui o id do curso de graduação, lato-sensu ou técnico ao qual esta seção pertence */
	@ManyToOne
	@JoinColumn(name = "id_curso", nullable = true)
	private Curso curso;

	/** Atribui o um título a seção. */
	private String titulo;

	/** Atribui o um texto que descreve seção. */
	private String descricao;

	/** Atribui o identificador registro entrada de quem e quando cadastrou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Atribui a data de cadastro da seção. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@CriadoEm
	private Date dataCadastro;

	/** Atribui o identificador registro de atualização de quem e quando alterou os dados da seção. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao", unique = false, nullable = true, insertable = true, updatable = true)
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Atribui a data em que a seção foi atualizada. */	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@AtualizadoEm
	private Date dataAtualizacao;

	/** Atribui um idioma a notícia (pt_BR, en_US e fr_FR). */	
	private String locale;
	
	/** Atribui o valor true quando a seção estiver publicada (disponível nos portais públicos). */
	private Boolean publicada;

	/** Atribui um título resumido da seção. */
	@Transient
	private String tituloResumido;

	/** Atribui um endereço eletrônico quando esta remete a um site externo.. */
	@Column(name = "link_externo", unique = false, nullable = true, insertable = true, updatable = true)
	private String linkExterno;

	/** Atribui o valor true quando a seção se remete a um site externo.. */
	@Transient
	private Boolean isLinkExterno;
	
	public SecaoExtraSite() {

	}

	public SecaoExtraSite(String titulo, String descricao, String linkExterno,
			Integer idArquivo) {
		this.titulo = titulo;
		this.descricao = descricao;
		this.linkExterno = linkExterno;
	}

	public Boolean getIsLinkExterno() {
		return this.isLinkExterno;
	}

	public void setIsLinkExterno(Boolean isLinkExterno) {
		this.isLinkExterno = isLinkExterno;
	}

	public String getLinkExterno() {
		return this.linkExterno;
	}

	public void setLinkExterno(String linkExterno) {
		this.linkExterno = linkExterno;
	}

	public static int getTAMANHO_MAXIMO() {
		return TAMANHO_MAXIMO;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTituloResumido(String tituloResumido) {
		this.tituloResumido = tituloResumido;
	}

	public String getTituloResumido() {
		return this.tituloResumido;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
		ValidatorUtil.validateRequired(getTitulo(), "Título da Seção", lista);
		if(this.isLinkExterno == false)
			ValidatorUtil.validateRequired(getDescricao(), "Conteúdo da Seção",lista);
		if(this.isLinkExterno == true)
			ValidatorUtil.validateRequired(getLinkExterno(), "Link Externo",lista);
		return lista;
	}

	public String getDescricaoResumida() {
		String descricaoSemHtml = StringUtils.unescapeHTML(descricao
				.replaceAll("\\<.*?\\>", ""));
		if (descricaoSemHtml.length() > TAMANHO_MAXIMO) {
			return descricaoSemHtml.replaceAll("[\\n|\\r]", " ").substring(0,
					TAMANHO_MAXIMO)
					+ "...";
		}
		return descricaoSemHtml;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Boolean getPublicada() {
		return publicada;
	}

	public void setPublicada(Boolean publicada) {
		this.publicada = publicada;
	}
	
}
