/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '16/02/2011'
 *
 */
package br.ufrn.sigaa.ava.forum.dominio;

import java.util.ArrayList;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Representa uma mensagem em um F�rum. Uma mensagem � um t�pico ou uma resposta
 * de um t�pico de um f�rum, usada para comunica��o entre os participantes de
 * uma turma virtual ou f�rum de curso. Podem criar e visualizar a mensagem
 * qualquer docente ou discente que possua acesso ao f�rum ou a turma virtual.
 * 
 * @author Gleydson Lima
 * @author Ilueny Santos
 * 
 */
@Entity
@Table(name = "forum_mensagem", schema = "ava")
public class ForumGeralMensagem implements Validatable {

	/** Identificador �nico para objetos desta classe. */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "hibernate_sequence") })
	@Column(name = "id_forum_mensagem", nullable = false)
	private int id;

	/** T�tulo da Mensagem **/
	private String titulo;

	/** Conte�do da Mensagem **/
	private String conteudo;

	/** Arquivo anexado a uma mensagem **/
	@Column(name = "id_arquivo")
	private Integer idArquivo;

	/** Indica se � um tipo de f�rum v�lido. Utilizado para exclus�o l�gica. */
	@CampoAtivo
	private boolean ativo;

	/** Data de cadastro da mensagem. */
	@CriadoEm
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data")
	private Date data;

	/** Usu�rio autor da mensagem. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	/** F�rum ao qual pertence a mensagem **/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_forum")
	private ForumGeral forum;

	/** T�pico em que a mensagem foi cadastrada **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_topico")
	private ForumGeralMensagem topico;

	/** T�pico em que a mensagem foi cadastrada ou respondida **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_mensagem_pai")
	private ForumGeralMensagem mensagemPai;

	/** �ltima mensagem postada para o t�pico. **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_ultima_mensagem")
	private ForumGeralMensagem ultimaMensagem;

	/** N�mero de respostas que a mensagem recebeu. **/
	@Column(name = "total_respostas")
	private Integer totalRespostas = 0;
	
	/** Utilizado na ordena��o das mensagens do t�pico. */
	private String hierarquia;

	/** Respostas ao t�pico. **/
	@Transient
	private List<ForumGeralMensagem> respostas = new ArrayList<ForumGeralMensagem>();
	
    /** Arquivo Anexo. */
	@Transient
    private UploadedFile arquivo;
	
	/** Indica se a turma deve ser notificada.*/
	@Transient
	private boolean notificarTurma = true;


	/** Construtor padr�o. */
	public ForumGeralMensagem() {
	}

	/** Construtor simples. */
	public ForumGeralMensagem(int id) {
		this.id = id;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(titulo, "T�tulo", lista);		
		ValidatorUtil.validateRequired(conteudo, "Mensagem", lista);
		return lista;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public Integer getIdArquivo() {
		return idArquivo;
	}

	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public ForumGeral getForum() {
		return forum;
	}

	public void setForum(ForumGeral forum) {
		this.forum = forum;
	}

	public ForumGeralMensagem getTopico() {
		return topico;
	}

	public void setTopico(ForumGeralMensagem topico) {
		this.topico = topico;
	}

	public Integer getTotalRespostas() {
		return totalRespostas;
	}

	public void setTotalRespostas(Integer totalRespostas) {
		this.totalRespostas = totalRespostas;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public ForumGeralMensagem getUltimaMensagem() {
		return ultimaMensagem;
	}

	public void setUltimaMensagem(ForumGeralMensagem ultimaMensagem) {
		this.ultimaMensagem = ultimaMensagem;
	}

	@Override
	public String toString() {
		return titulo;
	}
	
	public String getHierarquia() {
		return hierarquia;
	}

	public void setHierarquia(String hierarquia) {
		this.hierarquia = hierarquia;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}

	public List<ForumGeralMensagem> getRespostas() {
		return respostas;
	}

	public void setRespostas(List<ForumGeralMensagem> respostas) {
		this.respostas = respostas;
	}

	public ForumGeralMensagem getMensagemPai() {
		return mensagemPai;
	}

	public void setMensagemPai(ForumGeralMensagem mensagemPai) {
		this.mensagemPai = mensagemPai;
	}

	public boolean isNotificarTurma() {
		return notificarTurma;
	}

	public void setNotificarTurma(boolean notificarTurma) {
		this.notificarTurma = notificarTurma;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(obj, this, "id", "titulo");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((titulo == null) ? 0 : titulo.hashCode());
		return result;
	}

	/** Informa se esta mensagem � um t�pico de f�rum. */
	public boolean isTipoTopico() {
		return forum != null && topico != null && topico.getId() == id && mensagemPai == null;
	}

	/** Informa se esta mensagem � uma resposta a um t�pico de f�rum. */
	public boolean isTipoResposta() {
		return forum != null && topico != null && mensagemPai != null;
	}


	/** Retorna o n�vel da mensagem respondida. */
	public int getNivel() {
		if (hierarquia != null){
			String[] h = hierarquia.split("\\.");
			return h.length;
		}
		return 0;
	}
	
	/** Verifica se o f�rum possui arquivo anexo. */
	public boolean isPossuiArquivoAnexo() {
		return ValidatorUtil.isNotEmpty(idArquivo);
	}
	
}
