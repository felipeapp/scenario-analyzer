/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/11/2008
 *
 */
package br.ufrn.sigaa.sites.dominio;

import java.util.ArrayList;
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
 * Classe de domínio que representa as notícias publicadas nos portais público do SIGAA (Centro, Departamento, Programa e Cursos). 
 *  
 * @author Mario Rizzi
 * 
 */
@Entity
@Table(name = "noticia_site", schema = "site")
public class NoticiaSite implements Validatable {

	/** Constantes que definem o número máximo de caracteres quando se imprime o texto da notícia. */
	public static final int TAMANHO_MAXIMO = 300;
	public static final int TAMANHO_MAXIMO_COORDENACAO_STRICTO = 1200;
	
	/** Constantes que definem o número máximo de caracteres no título resumido. */
	private static final Integer QTD_MAX_CARACTERES = 80;
	
	public static final int TAMANHO_MAXIMO_PEQUENO = 100;
	public static final int TAMANHO_MAXIMO_MEDIO = 150;
	public static final int TAMANHO_MAXIMO_GRANDE = 625; 
		
	public static final int FOTO_WIDTH_MAX = 280;
	public static final int FOTO_HEIGHT_MAX = 380;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id", nullable = false)
	private int id;

	/** Atribui o id do centro, departamento ou programa ao qual esta notícia pertence */
	@ManyToOne
	@JoinColumn(name = "id_unidade", nullable = true)
	private Unidade unidade;
	
	/** Atribui o id do curso de graduação, lato-sensu ou técnico ao qual esta notícia pertence */
	@ManyToOne
	@JoinColumn(name = "id_curso", nullable = true)
	private Curso curso;

	/** Atribui o um título a notícia. */
	private String titulo;

	/** Atribui o um texto que descreve notícia. */
	private String descricao;
	
	/** Atribui um identificador da imagem da foto da notícia. */
	@Column(name = "id_foto")
	private Integer idFoto;
	
	/** Atribui o valor true quando a notícia estiver publicada (disponível nos portais públicos). */
	@Column(name = "publicada")
	private boolean publicada;

	/** Atribui o identificador registro entrada de quem e quando cadastrou */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_cadastro", unique = false, nullable = true, insertable = true, updatable = true)
	@CriadoPor
	private RegistroEntrada registroCadastro;

	/** Atribui a data de cadastro da notícia. */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@CriadoEm
	private Date dataCadastro;

	/** Atribui o identificador registro de atualização de quem e quando alterou os dados da notícia. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_atualizacao", unique = false, nullable = true, insertable = true, updatable = true)
	@AtualizadoPor
	private RegistroEntrada registroAtualizacao;

	/** Atribui a data em que a notícia foi atualizada. */	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_atualizacao", unique = false, nullable = true, insertable = true, updatable = true, length = 4)
	@AtualizadoEm
	private Date dataAtualizacao;

	/** Atribui um identificador do arquivo anexado a notícia. */	
	@Column(name = "id_arquivo")
	private Integer idArquivo;

	/** Atribui um idioma a notícia (pt_BR, en_US e fr_FR). */	
	private String locale;

	/** Atribui um título resumido da notícia. */
	@Transient
	private String tituloResumido;

	/** Atribui o valor true quando o usuário desejar excluir o arquivo anexado a notícias. */
	@Transient
	private boolean excluiArquivo;

	public NoticiaSite() {
	}

	public NoticiaSite(String titulo, String descricao) {
		this.titulo = titulo;
		this.descricao = descricao;
	}

	public boolean isExcluiArquivo() {
		return excluiArquivo;
	}

	public void setExcluiArquivo(boolean excluiArquivo) {
		this.excluiArquivo = excluiArquivo;
	}

	public static int getTAMANHO_MAXIMO() {
		return TAMANHO_MAXIMO;
	}

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
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
		
		if(!ValidatorUtil.isEmpty(titulo))
			setTituloResumido(StringUtils.limitTxt(titulo, QTD_MAX_CARACTERES ));
		
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

	/**
	 * Verifica se o tipo da foto enviado é válido
	 * @author Mario Rizzi
	 * @param tipoArquivo
	 * @return
	 */
	public boolean validaExtensao(String tipoArquivo){
		ArrayList<String> tipos = new ArrayList<String>();
		tipos.add("image/pjpeg");
		tipos.add("image/jpeg");
		tipos.add("image/gif"); 
		tipos.add("image/png");
		if(tipos.indexOf(tipoArquivo)==-1)
			return false;
		else 
			return true;
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
		
		ValidatorUtil.validateRequired(titulo, "Título", lista);
		ValidatorUtil.validateRequired(descricao, "Descrição", lista);
		ValidatorUtil.validateRequired(locale, "Idioma", lista);
		ValidatorUtil.validateRequired(publicada, "Idioma", lista);
		
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
	
	public String getDescricaoPequena() {
		return StringUtils.limitTxt(StringUtils.unescapeHTML(
				this.descricao.replaceAll("\\<.*?\\>", "")),
				TAMANHO_MAXIMO_PEQUENO);
	}
	
	public String getDescricaoMedia() {
		return StringUtils.limitTxt(StringUtils.unescapeHTML(
				this.descricao.replaceAll("\\<.*?\\>", "")), 
				TAMANHO_MAXIMO_MEDIO);
	}
	
	public String getDescricaoGrande() {
		return StringUtils.limitTxt(StringUtils.unescapeHTML(
				this.descricao.replaceAll("\\<.*?\\>", "")), 
				TAMANHO_MAXIMO_GRANDE);
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public boolean isPublicada() {
		return publicada;
	}

	public void setPublicada(boolean publicada) {
		this.publicada = publicada;
	}

	public Integer getIdFoto() {
		return idFoto;
	}

	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}
	
}
