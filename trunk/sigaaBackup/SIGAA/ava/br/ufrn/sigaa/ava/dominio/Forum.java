/*
 * Sistema Integrado de Patrim�nio e Administra��o de Contratos
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 12/12/2006
 *
 */
package br.ufrn.sigaa.ava.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;

/**
 * Representa um Forum de discuss�o em uma determinada turma
 *
 * @author Gleydson Lima
 *
 */
@Entity
@Table(name = "forum", schema = "ava")
@HumanName(value="F�rum", genero='M')
public class Forum implements DominioTurmaVirtual {

	/** Constante que indica que o f�rum � de uma turma de componente curricular. */ 
	public static final short TURMA = 1;
	/** Constante que indica que o f�rum � mural. */
	public static final short MURAL = 2;
	/** Constante que indica que o f�rum � de pesquisa e extens�o. */
	public static final short PESQUISA = 3;
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_forum", nullable = false)
	private int id;
	/** Indica que o f�rum est� ativo. */
	private Boolean ativo;
	/** Descri��o do f�rum. */
	private String descricao;
	/** Indica que o f�rum � de um curso. */
	private Boolean curso;
	/** Indica que o f�rum � de um programa. */
	private Boolean programa;
	/** ID do coordenador do curso. */
	private int idCursoCoordenador;
	/** T�tulo do F�rum. */
	private String titulo;

	/** Indica qual o tipo do F�rum. */
	private short tipo;
	/** Indica que o f�rum possui t�picos. */
	private boolean topicos;
	/** Turma ao qual o f�rum est� associado. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma")
	private Turma turma;
	/** Programa de Ensino em Rede ao qual o f�rum est� associado. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_programa_rede")
	private ProgramaRede programaRede;
	/** Usu�rio respons�vel pelo f�rum. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	/** Data de cria��o do f�rum. */
	@CriadoEm
	@Column(name = "data_criacao")
	private Date data;
	/** Lista de mensagens do f�rum */
	@OneToMany(mappedBy = "forum", fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	private List<ForumMensagem> mensagens = new ArrayList<ForumMensagem>(0);
	/** Total de t�picos do f�rum */
	@Transient
	private int totalTopicos;
	/** Data da �ltima mensagem */
	@Transient
	private Date dataUltimaMensagem;
	/** N�vel de Ensino do f�rum */
	private Character nivel;
	/** Retorna uma descri��o textual do n�vel de ensino do f�rum */
	public String getNivelDescricao() {
		return NivelEnsino.getDescricao(nivel);
	}
	/** Construtor padr�o. */
	public Forum() {
	}

	/** Retorna a data de cria��o do f�rum.
	 * @return the data
	 */
	public Date getData() {
		return data;
	}

	/** Seta a data de cria��o do f�rum.
	 * @param data
	 *            the data to set
	 */
	public void setData(Date data) {
		this.data = data;
	}

	/** Retorna a descri��o do f�rum.
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/** Seta a descri��o do f�rum.
	 * @param descricao
	 *            the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** Retorna a chave prim�ria
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	
	/** Indica que o f�rum est� ativo.
	 * @return
	 */
	public Boolean getAtivo() { 
		return this.ativo; 
	}
	
	/** Seta que o f�rum est� ativo.
	 * @param ativo
	 */
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo; 
	}
	
	
	/** Retorna uma lista de mensagens do f�rum
	 * @return the mensagens
	 */
	public List<ForumMensagem> getMensagens() {
		return mensagens;
	}

	/** Seta uma lista de mensagens do f�rum
	 * @param mensagens
	 *            the mensagens to set
	 */
	public void setMensagens(List<ForumMensagem> mensagens) {
		this.mensagens = mensagens;
	}

	/** Retorna o t�tulo do F�rum.
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}

	/** Seta o t�tulo do F�rum.
	 * @param titulo
	 *            the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/** Retorna a turma ao qual o f�rum est� associado.
	 * @return the turma
	 */
	public Turma getTurma() {
		return turma;
	}

	/** Seta a turma ao qual o f�rum est� associado.
	 * @param turma
	 *            the turma to set
	 */
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/** Retorna o usu�rio respons�vel pelo f�rum.
	 * @return the usuario
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/** Seta o usu�rio respons�vel pelo f�rum.
	 * @param usuario
	 *            the usuario to set
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/** Valida os dados do f�rum.
	 * @return
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		if (titulo == null || "".equals(titulo.trim()))
			lista.addErro("O t�tulo do f�rum n�o pode ficar em branco.");
		if (descricao == null || "".equals(descricao.trim()))
			lista.addErro("A descri��o do f�rum n�o pode ficar em branco.");

		if (titulo != null && !"".equals(titulo.trim()) && titulo.length() < 3)
			lista.addErro("O t�tulo deve conter ao menos tr�s caracteres");
		if (descricao != null && !"".equals(descricao.trim()) && descricao.length() < 3)
			lista.addErro("A descri��o deve conter ao menos tr�s caracteres");

		return lista;
	}

	/** Retorna a data da �ltima mensagem
	 * @return
	 */
	public Date getDataUltimaMensagem() {
		return dataUltimaMensagem;
	}

	/** Retorna o total de t�picos do f�rum
	 * @return the totalTopicos
	 */
	public int getTotalTopicos() {
		return totalTopicos;
	}

	/** Seta o total de t�picos do f�rum
	 * @param totalTopicos
	 *            the totalTopicos to set
	 */
	public void setTotalTopicos(int totalTopicos) {
		this.totalTopicos = totalTopicos;
	}

	/** Seta a data da �ltima mensagem
	 * @param dataUltimaMensagem
	 *            the dataUltimaMensagem to set
	 */
	public void setDataUltimaMensagem(Date dataUltimaMensagem) {
		this.dataUltimaMensagem = dataUltimaMensagem;
	}

	/** Retorna  o tipo do F�rum.
	 * @return
	 */
	public short getTipo() {
		return tipo;
	}

	/** Seta  o tipo do F�rum.
	 * @param tipo
	 */
	public void setTipo(short tipo) {
		this.tipo = tipo;
	}

	/** Indica que o f�rum possui t�picos.
	 * @return the topicos
	 */
	public boolean isTopicos() {
		return topicos;
	}

	/** Seta a indica��o que o f�rum possui t�picos.
	 * @param topicos the topicos to set
	 */
	public void setTopicos(boolean topicos) {
		this.topicos = topicos;
	}

	/** Indica se o f�rum � um mural.
	 * @return
	 */
	public boolean isMural() {
		return this.tipo == MURAL;
	}

	/** Indica que o f�rum � de um curso.
	 * @return
	 */
	public Boolean getCurso() {
		return curso;
	}

	/** Seta a indica��o que o f�rum � de um curso.
	 * @param curso
	 */
	public void setCurso(Boolean curso) {
		this.curso = curso;
	}

	/** Retorna o ID do coordenador do curso.
	 * @return
	 */
	public int getIdCursoCoordenador() {
		return idCursoCoordenador;
	}

	/** Seta o ID do coordenador do curso.
	 * @param idCursoCoordenador
	 */
	public void setIdCursoCoordenador(int idCursoCoordenador) {
		this.idCursoCoordenador = idCursoCoordenador;
	}

	/** Retorna uma mensagem indicando que um novo f�rum foi cadastrado.
	 * @see br.ufrn.sigaa.ava.dominio.DominioTurmaVirtual#getMensagemAtividade()
	 */
	public String getMensagemAtividade() {
		return "Novo f�rum cadastrado";
	}

	/** Retorna o n�vel de ensino do f�rum
	 * @return
	 */
	public Character getNivel() {
		return nivel;
	}

	/** Seta o n�vel de ensino do f�rum
	 * @param nivel
	 */
	public void setNivel(Character nivel) {
		this.nivel = nivel;
	}

	/** Seta que o f�rum � de um programa.
	 * @param programa
	 */
	public void setPrograma(Boolean programa) {
		this.programa = programa;
	}

	/** Indica que o f�rum � de um programa.
	 * @return
	 */
	public Boolean getPrograma() {
		return programa;
	}

	/** Seta o rograma de Ensino em Rede ao qual o f�rum est� associado.
	 * @param programaRede
	 */
	public void setProgramaRede(ProgramaRede programaRede) {
		this.programaRede = programaRede;
	}

	/** Retorna o rograma de Ensino em Rede ao qual o f�rum est� associado.
	 * @return
	 */
	public ProgramaRede getProgramaRede() {
		return programaRede;
	}
	
}
