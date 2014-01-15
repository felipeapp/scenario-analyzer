/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 14/09/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.dominio;

import java.util.Date;
import java.util.List;

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

import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.ava.dominio.DominioTurmaVirtual;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;
import br.ufrn.sigaa.parametros.dominio.ParametrosTurmaVirtual;

/**
 * Representa uma mensagem em um Forum de nível médio.
 * Uma mensagem é um tópico ou uma resposta de um tópico de um fórum, 
 * usada para comunicação entre os participantes de uma turma virtual ou fórum de curso.
 * Podem criar e visualizar a mensagem qualquer docente ou discente que possua acesso ao fórum ou a turma virtual.
 * 
 * @author Rafael Gomes
 * 
 */
@Entity
@Table(name = "forum_mensagem_medio", schema = "medio")
public class ForumMensagemMedio implements DominioTurmaVirtual {

	/** Chave primaria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", 
			parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_forum_mensagem_medio", nullable = false)
	private int id;

	/** Fórum ao qual pertence a mensagem **/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_forum_medio")
	private ForumMedio forum;
	
	/** Referência o tópico de aula ao qual este fórum está associado. */
	@ManyToOne
	@JoinColumn(name = "id_topico_aula", nullable = true )
	private TopicoAula topicoAula;

	/** Título da Mensagem **/
	private String titulo;

	/** Conteúdo da Mensagem **/
	private String conteudo;

	/** Se é um fórum de TurmaSerie **/
	@Column(name = "turma_serie")
	private Boolean turmaSerie;
	
	/** Arquivo anexado a uma mensagem **/
	@Column(name = "id_arquivo")
	private Integer idArquivo;

	/** Usuário que cadastrou a mensagem **/ 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_usuario")
	@CriadoPor
	private Usuario usuario;

	/** Data em que a mensagem foi cadastrada **/
	@CriadoEm
	private Date data;

	/** Tópico em que a mensagem foi cadastrada (Mensagem Pai) **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_topico")
	private ForumMensagemMedio topico;

	/** Número de respostas que a mensagem recebeu **/
	private Integer respostas = 0;
	
	/** Guarda a data da última postagem no tópico **/
	@Column(name="ultima_postagem")
	private Date ultimaPostagem;

	/** Indica se a resposta está ativa ou foi desativada por quem criou **/
	private boolean ativo = true;
	
	/**
	 * Lista de matrículas dos vínculos ativos do discente que 
	 * postou a mensagem no fórum (caso o usuário seja um discente).
	 */
	@Transient
	private List<Long> matriculas;
	
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();

		if (titulo == null || "".equals(titulo.trim()))
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Título da mensagem");

		if (titulo != null && titulo.length() > ParametroHelper.getInstance().getParametroInt(ParametrosTurmaVirtual.TAMANHO_MAXIMO_TITULO_MENSAGEM_FORUM) )
			mensagens.addMensagem(MensagensTurmaVirtual.TAMANHO_TEXTO_MAIOR_MAXIMO, "Título", titulo.length(), ParametroHelper.getInstance().getParametroInt(ParametrosTurmaVirtual.TAMANHO_MAXIMO_TITULO_MENSAGEM_FORUM));
		

		if (conteudo == null || "".equals(conteudo.trim()))
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Conteúdo da mensagem");

		return mensagens;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public ForumMedio getForum() {
		return forum;
	}

	public void setForum(ForumMedio forum) {
		this.forum = forum;
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

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public ForumMensagemMedio getTopico() {
		return topico;
	}

	public void setTopico(ForumMensagemMedio topico) {
		this.topico = topico;
	}

	public Integer getRespostas() {
		return respostas;
	}

	/**
	 * @param respostas
	 *            the respostas to set
	 */
	public void setRespostas(Integer respostas) {
		this.respostas = respostas;
	}

	public String getMensagemAtividade() {
		return "Nova mensagem no fórum " + forum.getTitulo();
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Date getUltimaPostagem() {
		return ultimaPostagem;
	}

	public void setUltimaPostagem(Date ultimaPostagem) {
		this.ultimaPostagem = ultimaPostagem;
	}

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	/**
	 * @return the matriculas
	 */
	public List<Long> getMatriculas() {
		return matriculas;
	}

	/**
	 * @param matriculas the matriculas to set
	 */
	public void setMatriculas(List<Long> matriculas) {
		this.matriculas = matriculas;
	}

	public TopicoAula getTopicoAula() {
		return topicoAula;
	}

	public void setTopicoAula(TopicoAula topicoAula) {
		this.topicoAula = topicoAula;
	}
	
	@Override
	public boolean equals(Object o){
		return EqualsUtil.testTransientEquals(this, o, "id");
	}
	
	@Override
	public int hashCode (){
		return HashCodeUtil.hashAll(id);
	}

	@Override
	public Turma getTurma() {
		return null;
	}

	@Override
	public void setTurma(Turma turma) {
		
	}

	public Boolean getTurmaSerie() {
		return turmaSerie;
	}

	public void setTurmaSerie(Boolean turmaSerie) {
		this.turmaSerie = turmaSerie;
	}
}
