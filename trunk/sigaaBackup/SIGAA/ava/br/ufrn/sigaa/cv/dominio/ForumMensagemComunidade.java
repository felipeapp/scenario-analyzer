/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 12/12/2006
 *
 */
package br.ufrn.sigaa.cv.dominio;

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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Representa uma mensagem em um fórum
 * 
 * @author Gleydson Lima
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "forum_mensagem", schema = "cv")
public class ForumMensagemComunidade implements DominioComunidadeVirtual {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_forum_mensagem", nullable = false)
	private int id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_forum")
	private ForumComunidade forum;

	private String titulo;

	private String conteudo;

	@Column(name = "id_arquivo")
	private Integer idArquivo;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_usuario")
	@CriadoPor
	private Usuario usuario;

	@CriadoEm
	private Date data;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_topico")
	private ForumMensagemComunidade topico;

	private int respostas;
	
	@Column(name="ultima_postagem")
	private Date ultimaPostagem;

	private boolean ativo = true;

	public ListaMensagens validate() {
		ListaMensagens mensagens = new ListaMensagens();

		if (titulo == null || "".equals(titulo.trim()))
			mensagens.addErro("O título da mensagem é obrigatório!");

		if (titulo != null && titulo.length() > 200)
			mensagens.addErro("O título da mensagem está com "
					+ titulo.length()
					+ " caracteres, mas só pode ter no máximo 200.");

		if (conteudo == null || "".equals(conteudo.trim()))
			mensagens.addErro("O conteúdo da mensagem é obrigatório!");

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

	public ForumComunidade getForum() {
		return forum;
	}

	public void setForum(ForumComunidade forum) {
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
	public ForumMensagemComunidade getTopico() {
		return topico;
	}

	/**
	 * @param topico
	 *            the topico to set
	 */
	public void setTopico(ForumMensagemComunidade topico) {
		this.topico = topico;
	}

	/**
	 * @return the respostas
	 */
	public int getRespostas() {
		return respostas;
	}

	/**
	 * @param respostas
	 *            the respostas to set
	 */
	public void setRespostas(int respostas) {
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

	public ComunidadeVirtual getComunidade() {
		return null;
	}

	public void setComunidade(ComunidadeVirtual comunidade) {
		
	}

}
