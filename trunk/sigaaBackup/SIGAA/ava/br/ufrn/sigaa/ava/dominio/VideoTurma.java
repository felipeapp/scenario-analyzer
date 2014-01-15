/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe de dom�nio que representa um v�deo a ser exibido na turma virtual.
 * 
 */
@Entity @HumanName(value="V�deo", genero='M') 
@Table(name="video_turma", schema="ava")
public class VideoTurma extends AbstractMaterialTurma implements DominioTurmaVirtual {
	
	/** Chave Prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_video_turma", nullable = false)
	private int id;

	/** T�tulo. */
	private String titulo;
	
	/** A descri��o do v�deo. */
	private String descricao;

	/** A turma � qual pertence este v�deo. */
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_turma")
	private Turma turma;
	
	/** A altura do player do v�deo. */
	private int altura;
	
	/** Indica se o v�deo deve iniciar em tela cheia. */
	@Column(name="tela_cheia")
	private boolean telaCheia;
	
	/** O link para a licaliza��o do v�deo na internet. */
	private String link;
	
	/** Identificador para o arquivo contendo o v�deo. */
	@Column(name="id_arquivo")
	private Integer idArquivo;
	
	/** Identificador para o arquivo contendo o v�deo. */
	@Column(name="id_arquivo_convertido")
	private Integer idArquivoConvertido;
	
	/** Indica se o v�deo enviado deve ser convertido. */
	private boolean converter = false;
	
	/** Content type do arquivo de v�deo apontado pelo idArquivo. */
	@Column(name="content_type")
	private String contentType;
	
	/** Indica se � para abrir um popup com o v�deo. Em caso negativo, o v�deo � exibido diretamente na turma virtual. */
	@Column (name="abrir_em_nova_janela")
	private boolean abrirEmNovaJanela;
	
	/** Referencia o t�pico de aula ao qual este v�deo est� associado. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_topico_aula", nullable = true )
	private TopicoAula topicoAula = new TopicoAula();
	
	/** Usu�rio que cadastrou o v�deo.  */
	@CriadoPor @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="id_usuario")
	private Usuario usuario;

	/** Data de cadastro do v�deo. */
	@CriadoEm @Column(name="data_criacao")
	private Date data;
	
	/** Data da �ltima tentativa de convers�o do v�deo. */
	@Column(name="data_ultima_tentativa_conversao")
	private Date dataUltimaTentativaConversao;
	
	/** Indica se o v�deo est� ativo ou foi removido. */
	@CampoAtivo
	private boolean ativo = true;
	
	/** Material Turma. */
	@ManyToOne(fetch=FetchType.LAZY, cascade= CascadeType.ALL)
	@JoinColumn(name = "id_material_turma")
	private MaterialTurma material = new MaterialTurma(TipoMaterialTurma.VIDEO);
	
	/** Indica se houve erro na convers�o. */
	private boolean erro;
	
	/** Mensagem que o conversor envia ap�s a opera��o de convers�o. */
	@Column(name="mensagem_conversao")
	private String mensagemConversao;
	
	/** Vari�vel est�tica que armazena as resolu��es dispon�veis para a exibi��o dos v�deos. */
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
	 * Este m�todo retorna a url do player do v�deo externo. Atualmente, s� suporta Youtube e Vimeo.
	 * Caso o docente indique um v�deo externo fora desses dois dom�nios, o m�todo retorna null.
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
		
		// No momento, s� aceitamos links do vimeo e youtube. As urls devem estar nos formatos especificados pelas express�es regulares abaixo.
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
		return "Novo V�deo: " + titulo;
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