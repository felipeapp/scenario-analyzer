/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/01/2008
 *
 */
package br.ufrn.sigaa.ava.dominio;

import java.util.Date;
import java.util.List;

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

import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.mensagens.MensagensTurmaVirtual;
import br.ufrn.sigaa.parametros.dominio.ParametrosTurmaVirtual;

/**
 * Representa uma mensagem em um Forum.
 * Uma mensagem � um t�pico ou uma resposta de um t�pico de um f�rum, 
 * usada para comunica��o entre os participantes de uma turma virtual ou f�rum de curso.
 * Podem criar e visualizar a mensagem qualquer docente ou discente que possua acesso ao f�rum ou a turma virtual.
 * 
 * @author Gleydson Lima
 * 
 */
@Entity
@Table(name = "forum_mensagem", schema = "ava")
public class ForumMensagem implements DominioTurmaVirtual {

	/** Chave primaria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_forum_mensagem", nullable = false)
	private int id;

	/** F�rum ao qual pertence a mensagem **/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_forum")
	private Forum forum;
	
	/** Refer�ncia o t�pico de aula ao qual este f�rum est� associado. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_topico_aula", nullable = true )
	private TopicoAula topicoAula;

	/** T�tulo da Mensagem **/
	private String titulo;

	/** Conte�do da Mensagem **/
	private String conteudo;

	/** Se � um f�rum de curso **/
	private Boolean curso;
	
	/** Arquivo anexado a uma mensagem **/
	@Column(name = "id_arquivo")
	private Integer idArquivo;

	/** Usu�rio que cadastrou a mensagem **/ 
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_usuario")
	@CriadoPor
	private Usuario usuario;

	/** Data em que a mensagem foi cadastrada **/
	@CriadoEm
	private Date data;

	/** T�pico em que a mensagem foi cadastrada (Mensagem Pai) **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_topico")
	private ForumMensagem topico;

	/** N�mero de respostas que a mensagem recebeu **/
	private Integer respostas = 0;
	
	/** Guarda a data da �ltima postagem no t�pico **/
	@Column(name="ultima_postagem")
	private Date ultimaPostagem;

	/** Indica se a resposta est� ativa ou foi desativada por quem criou **/
	private boolean ativo = true;
	
	/**
	 * Lista de matr�culas dos v�nculos ativos do discente que 
	 * postou a mensagem no f�rum (caso o usu�rio seja um discente).
	 */
	@Transient
	private List<Long> matriculas;
	
	/**
	 * Lista de denuncias associadas a mensagem no f�rum.
	 */
	@OneToMany(cascade = { CascadeType.ALL },fetch = FetchType.LAZY, mappedBy="forumMensagem") 
	private List<DenunciaMensagem> denunciasMensagem;
	
	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();

		if (titulo == null || "".equals(titulo.trim()))
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "T�tulo da mensagem");

		if (titulo != null && titulo.length() > ParametroHelper.getInstance().getParametroInt(ParametrosTurmaVirtual.TAMANHO_MAXIMO_TITULO_MENSAGEM_FORUM) )
			mensagens.addMensagem(MensagensTurmaVirtual.TAMANHO_TEXTO_MAIOR_MAXIMO, "T�tulo", titulo.length(), ParametroHelper.getInstance().getParametroInt(ParametrosTurmaVirtual.TAMANHO_MAXIMO_TITULO_MENSAGEM_FORUM));
		

		if (conteudo == null || "".equals(conteudo.trim()))
			mensagens.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Conte�do da mensagem");

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

	public Forum getForum() {
		return forum;
	}

	public void setForum(Forum forum) {
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

	/**
	 * @return the topico
	 */
	public ForumMensagem getTopico() {
		return topico;
	}

	/**
	 * @param topico
	 *            the topico to set
	 */
	public void setTopico(ForumMensagem topico) {
		this.topico = topico;
	}

	/**
	 * @return the respostas
	 */
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

	public Boolean getCurso() {
		return curso;
	}

	public void setCurso(Boolean curso) {
		this.curso = curso;
	}
	
	/**
	 * Retorna a turma do grupo.
	 * @Deprecated N�o usar. Foi criado por imposi��o da interface mas n�o est� implementado.
	 */
	@Deprecated
	public Turma getTurma() {
		return null;
	}

	/**
	 * Seta a turma do grupo
	 * @Deprecated N�o usar. Foi criado por imposi��o da interface mas n�o est� implementado.
	 */
	@Deprecated
	public void setTurma(Turma turma) {
		
	}

	public String getMensagemAtividade() {
		return "Nova mensagem no f�rum " + forum.getTitulo();
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
	
	/** Informa se esta mensagem � um t�pico de f�rum. */
	public boolean isTipoTopico() {
		return forum != null && topico != null && topico.getId() == id;
	}
	
	public String getDescricaoTipo(){
		if( this.isTipoTopico() )
			return "T�pico";
		else
			return "Resposta do T�pico";
	}
	
	@Override
	public boolean equals(Object o){
		return EqualsUtil.testTransientEquals(this, o, "id");
	}
	
	@Override
	public int hashCode (){
		return HashCodeUtil.hashAll(id);
	}
}
