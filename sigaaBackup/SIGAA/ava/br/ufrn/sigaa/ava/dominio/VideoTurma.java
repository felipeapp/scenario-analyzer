/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/02/2011
 *
 */
package br.ufrn.sigaa.ava.dominio;


import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections.map.ListOrderedMap;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;


/**
 * Classe de domínio que representa um vídeo a ser exibido na turma virtual.
 * 
 */
@Entity @HumanName(value="Vídeo", genero='M') 
@Table(name="video_turma", schema="ava")
public class VideoTurma extends AbstractMaterialTurma implements DominioTurmaVirtual {
	
	/** Chave Primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_video_turma", nullable = false)
	private int id;

	/** Título. */
	private String titulo;
	
	/** A descrição do vídeo. */
	private String descricao;

	/** A turma à qual pertence este vídeo. */
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_turma")
	private Turma turma;
	
	/** A altura do player do vídeo. */
	private int altura;
	
	/** Indica se o vídeo deve iniciar em tela cheia. */
	@Column(name="tela_cheia")
	private boolean telaCheia;
	
	/** O link para a licalização do vídeo na internet. */
	private String link;
	
	/** Identificador para o arquivo contendo o vídeo. */
	@Column(name="id_arquivo")
	private Integer idArquivo;
	
	/** Identificador para o arquivo contendo o vídeo. */
	@Column(name="id_arquivo_convertido")
	private Integer idArquivoConvertido;
	
	/** Indica se o vídeo enviado deve ser convertido. */
	private boolean converter = false;
	
	/** Content type do arquivo de vídeo apontado pelo idArquivo. */
	@Column(name="content_type")
	private String contentType;
	
	/** Indica se é para abrir um popup com o vídeo. Em caso negativo, o vídeo é exibido diretamente na turma virtual. */
	@Column (name="abrir_em_nova_janela")
	private boolean abrirEmNovaJanela;
	
	/** Referencia o tópico de aula ao qual este vídeo está associado. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_topico_aula", nullable = true )
	private TopicoAula topicoAula = new TopicoAula();
	
	/** Usuário que cadastrou o vídeo.  */
	@CriadoPor @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_usuario")
	private Usuario usuario;

	/** Data de cadastro do vídeo. */
	@CriadoEm @Column(name="data_criacao")
	private Date data;
	
	/** Data da última tentativa de conversão do vídeo. */
	@Column(name="data_ultima_tentativa_conversao")
	private Date dataUltimaTentativaConversao;
	
	/** Indica se o vídeo está ativo ou foi removido. */
	@CampoAtivo
	private boolean ativo = true;
	
	/** Material Turma. */
	@ManyToOne(fetch=FetchType.LAZY, cascade= CascadeType.ALL)
	@JoinColumn(name = "id_material_turma")
	private MaterialTurma material = new MaterialTurma(TipoMaterialTurma.VIDEO);
	
	/** Indica se houve erro na conversão. */
	private boolean erro;
	
	/** Mensagem que o conversor envia após a operação de conversão. */
	@Column(name="mensagem_conversao")
	private String mensagemConversao;
	
	/** Variável estática que armazena as resoluções disponíveis para a exibição dos vídeos. */
	@Transient
	private static Map <Integer, Integer> resolucoes;
	
	static {
		@SuppressWarnings("unchecked")
		Map <Integer, Integer> auxResolucoes = new ListOrderedMap ();
		
		auxResolucoes.put (150, 266);
		auxResolucoes.put (280, 497);
		auxResolucoes.put (480, 853);
		
		resolucoes = auxResolucoes;
	}
	
	/**
	 * Este método retorna a url do player do vídeo externo. Atualmente, só suporta Youtube e Vimeo.
	 * Caso o docente indique um vídeo externo fora desses dois domínios, o método retorna null.
	 * 
	 * @return
	 */
	public String getLinkVideo () {
		
		// Formatos das urls dos players do youtube e vimeo
		String vimeo = "https://vimeo.com/moogaloop.swf?clip_id=%s&server=vimeo.com&show_title=1&show_bylin";
		String youtube = "https://www.youtube.com/v/%s";
		
		String linkVideo = "";
		
		boolean isVimeo = false;
		
		if (StringUtils.isEmpty(link))
			return null;
		
		Pattern pattern = null;
		
		// No momento, só aceitamos links do vimeo e youtube. As urls devem estar nos formatos especificados pelas expressões regulares abaixo.
		if (link.contains("vimeo")){
			pattern = Pattern.compile("(https?://)?(www\\.)?vimeo\\.com/([0-9]+)/?");
			isVimeo = true;
		} else if (link.contains("youtube"))
			pattern = Pattern.compile("(https?://)?(www\\.)?youtube\\.com(\\.br)?/watch\\?.*v=([^&]+).*");
		else
			return null;
		
		Matcher m = pattern.matcher(link);
		
		if (!m.find())
			return null;
		
		if (isVimeo)
			linkVideo = String.format(vimeo, m.group(3));
		else
			linkVideo = String.format(youtube, m.group(4));
		
		return linkVideo;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getMensagemAtividade() {
		return "Novo Vídeo: " + titulo;
	}

	public TopicoAula getTopicoAula() {
		return topicoAula;
	}

	public void setTopicoAula(TopicoAula topicoAula) {
		this.topicoAula = topicoAula;
	}

	@Override
	public Turma getTurma() {
		return turma;
	}

	@Override
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	@Override
	public String getNome() {
		return getTitulo();
	}

	@Override
	public Usuario getUsuarioCadastro() {
		return getUsuario();
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	@Override
	public Date getDataCadastro() {
		return getData();
	}

	public boolean isAbrirEmNovaJanela() {
		return abrirEmNovaJanela;
	}

	public void setAbrirEmNovaJanela(boolean abrirEmNovaJanela) {
		this.abrirEmNovaJanela = abrirEmNovaJanela;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getAltura() {
		return altura;
	}
	
	public int getLargura () {
		return getResolucoes().get(getAltura());
	}
	
	public static Map <Integer, Integer> getResolucoes () {
		return resolucoes;
	}

	public void setAltura(int altura) {
		this.altura = altura;
	}
	
	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public boolean isTelaCheia() {
		return telaCheia;
	}

	public void setTelaCheia(boolean telaCheia) {
		this.telaCheia = telaCheia;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public MaterialTurma getMaterial() {
		return material;
	}

	public void setMaterial(MaterialTurma material) {
		this.material = material;
	}

	public Integer getIdArquivoConvertido() {
		return idArquivoConvertido;
	}

	public void setIdArquivoConvertido(Integer idArquivoConvertido) {
		this.idArquivoConvertido = idArquivoConvertido;
	}

	public boolean isConverter() {
		return converter;
	}

	public void setConverter(boolean converter) {
		this.converter = converter;
	}
	
	public TopicoAula getAula () {
		return getTopicoAula();
	}

	public boolean isErro() {
		return erro;
	}

	public void setErro(boolean erro) {
		this.erro = erro;
	}

	public String getMensagemConversao() {
		return mensagemConversao;
	}

	public void setMensagemConversao(String mensagemConversao) {
		this.mensagemConversao = mensagemConversao;
	}

	@Override
	public String getDescricaoGeral() {
		return descricao;
	}

	public Date getDataUltimaTentativaConversao() {
		return dataUltimaTentativaConversao;
	}

	public void setDataUltimaTentativaConversao(Date dataUltimaTentativaConversao) {
		this.dataUltimaTentativaConversao = dataUltimaTentativaConversao;
	}
}