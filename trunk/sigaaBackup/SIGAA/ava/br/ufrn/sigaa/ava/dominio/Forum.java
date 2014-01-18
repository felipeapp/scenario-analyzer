/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
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
 * Representa um Forum de discussão em uma determinada turma
 *
 * @author Gleydson Lima
 *
 */
@Entity
@Table(name = "forum", schema = "ava")
@HumanName(value="Fórum", genero='M')
public class Forum implements DominioTurmaVirtual {

	/** Constante que indica que o fórum é de uma turma de componente curricular. */ 
	public static final short TURMA = 1;
	/** Constante que indica que o fórum é mural. */
	public static final short MURAL = 2;
	/** Constante que indica que o fórum é de pesquisa e extensão. */
	public static final short PESQUISA = 3;
	/** Chave primária. */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_forum", nullable = false)
	private int id;
	/** Indica que o fórum está ativo. */
	private Boolean ativo;
	/** Descrição do fórum. */
	private String descricao;
	/** Indica que o fórum é de um curso. */
	private Boolean curso;
	/** Indica que o fórum é de um programa. */
	private Boolean programa;
	/** ID do coordenador do curso. */
	private int idCursoCoordenador;
	/** Título do Fórum. */
	private String titulo;

	/** Indica qual o tipo do Fórum. */
	private short tipo;
	/** Indica que o fórum possui tópicos. */
	private boolean topicos;
	/** Turma ao qual o fórum está associado. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma")
	private Turma turma;
	/** Programa de Ensino em Rede ao qual o fórum está associado. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_programa_rede")
	private ProgramaRede programaRede;
	/** Usuário responsável pelo fórum. */
	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	/** Data de criação do fórum. */
	@CriadoEm
	@Column(name = "data_criacao")
	private Date data;
	/** Lista de mensagens do fórum */
	@OneToMany(mappedBy = "forum", fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	private List<ForumMensagem> mensagens = new ArrayList<ForumMensagem>(0);
	/** Total de tópicos do fórum */
	@Transient
	private int totalTopicos;
	/** Data da última mensagem */
	@Transient
	private Date dataUltimaMensagem;
	/** Nível de Ensino do fórum */
	private Character nivel;
	/** Retorna uma descrição textual do nível de ensino do fórum */
	public String getNivelDescricao() {
		return NivelEnsino.getDescricao(nivel);
	}
	/** Construtor padrão. */
	public Forum() {
	}

	/** Retorna a data de criação do fórum.
	 * @return the data
	 */
	public Date getData() {
		return data;
	}

	/** Seta a data de criação do fórum.
	 * @param data
	 *            the data to set
	 */
	public void setData(Date data) {
		this.data = data;
	}

	/** Retorna a descrição do fórum.
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/** Seta a descrição do fórum.
	 * @param descricao
	 *            the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** Retorna a chave primária
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave primária
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	
	/** Indica que o fórum está ativo.
	 * @return
	 */
	public Boolean getAtivo() { 
		return this.ativo; 
	}
	
	/** Seta que o fórum está ativo.
	 * @param ativo
	 */
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo; 
	}
	
	
	/** Retorna uma lista de mensagens do fórum
	 * @return the mensagens
	 */
	public List<ForumMensagem> getMensagens() {
		return mensagens;
	}

	/** Seta uma lista de mensagens do fórum
	 * @param mensagens
	 *            the mensagens to set
	 */
	public void setMensagens(List<ForumMensagem> mensagens) {
		this.mensagens = mensagens;
	}

	/** Retorna o título do Fórum.
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}

	/** Seta o título do Fórum.
	 * @param titulo
	 *            the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/** Retorna a turma ao qual o fórum está associado.
	 * @return the turma
	 */
	public Turma getTurma() {
		return turma;
	}

	/** Seta a turma ao qual o fórum está associado.
	 * @param turma
	 *            the turma to set
	 */
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/** Retorna o usuário responsável pelo fórum.
	 * @return the usuario
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/** Seta o usuário responsável pelo fórum.
	 * @param usuario
	 *            the usuario to set
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/** Valida os dados do fórum.
	 * @return
	 */
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();

		if (titulo == null || "".equals(titulo.trim()))
			lista.addErro("O título do fórum não pode ficar em branco.");
		if (descricao == null || "".equals(descricao.trim()))
			lista.addErro("A descrição do fórum não pode ficar em branco.");

		if (titulo != null && !"".equals(titulo.trim()) && titulo.length() < 3)
			lista.addErro("O título deve conter ao menos três caracteres");
		if (descricao != null && !"".equals(descricao.trim()) && descricao.length() < 3)
			lista.addErro("A descrição deve conter ao menos três caracteres");

		return lista;
	}

	/** Retorna a data da última mensagem
	 * @return
	 */
	public Date getDataUltimaMensagem() {
		return dataUltimaMensagem;
	}

	/** Retorna o total de tópicos do fórum
	 * @return the totalTopicos
	 */
	public int getTotalTopicos() {
		return totalTopicos;
	}

	/** Seta o total de tópicos do fórum
	 * @param totalTopicos
	 *            the totalTopicos to set
	 */
	public void setTotalTopicos(int totalTopicos) {
		this.totalTopicos = totalTopicos;
	}

	/** Seta a data da última mensagem
	 * @param dataUltimaMensagem
	 *            the dataUltimaMensagem to set
	 */
	public void setDataUltimaMensagem(Date dataUltimaMensagem) {
		this.dataUltimaMensagem = dataUltimaMensagem;
	}

	/** Retorna  o tipo do Fórum.
	 * @return
	 */
	public short getTipo() {
		return tipo;
	}

	/** Seta  o tipo do Fórum.
	 * @param tipo
	 */
	public void setTipo(short tipo) {
		this.tipo = tipo;
	}

	/** Indica que o fórum possui tópicos.
	 * @return the topicos
	 */
	public boolean isTopicos() {
		return topicos;
	}

	/** Seta a indicação que o fórum possui tópicos.
	 * @param topicos the topicos to set
	 */
	public void setTopicos(boolean topicos) {
		this.topicos = topicos;
	}

	/** Indica se o fórum é um mural.
	 * @return
	 */
	public boolean isMural() {
		return this.tipo == MURAL;
	}

	/** Indica que o fórum é de um curso.
	 * @return
	 */
	public Boolean getCurso() {
		return curso;
	}

	/** Seta a indicação que o fórum é de um curso.
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

	/** Retorna uma mensagem indicando que um novo fórum foi cadastrado.
	 * @see br.ufrn.sigaa.ava.dominio.DominioTurmaVirtual#getMensagemAtividade()
	 */
	public String getMensagemAtividade() {
		return "Novo fórum cadastrado";
	}

	/** Retorna o nível de ensino do fórum
	 * @return
	 */
	public Character getNivel() {
		return nivel;
	}

	/** Seta o nível de ensino do fórum
	 * @param nivel
	 */
	public void setNivel(Character nivel) {
		this.nivel = nivel;
	}

	/** Seta que o fórum é de um programa.
	 * @param programa
	 */
	public void setPrograma(Boolean programa) {
		this.programa = programa;
	}

	/** Indica que o fórum é de um programa.
	 * @return
	 */
	public Boolean getPrograma() {
		return programa;
	}

	/** Seta o rograma de Ensino em Rede ao qual o fórum está associado.
	 * @param programaRede
	 */
	public void setProgramaRede(ProgramaRede programaRede) {
		this.programaRede = programaRede;
	}

	/** Retorna o rograma de Ensino em Rede ao qual o fórum está associado.
	 * @return
	 */
	public ProgramaRede getProgramaRede() {
		return programaRede;
	}
	
}
