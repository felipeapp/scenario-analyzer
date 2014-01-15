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

	public static final short TURMA = 1;

	public static final short MURAL = 2;

	public static final short PESQUISA = 3;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_forum", nullable = false)
	private int id;
	
	private Boolean ativo;

	private String descricao;
	
	private Boolean curso;
	
	private int idCursoCoordenador;
	
	/**
	 * private Unidade unidade;
	 * 
	 * private Integer categoriaServidor;
	 */

	private String titulo;

	private short tipo;
	
	private boolean topicos;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma")
	private Turma turma;

	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	@CriadoEm
	@Column(name = "data_criacao")
	private Date data;

	@OneToMany(mappedBy = "forum", fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	private List<ForumMensagem> mensagens = new ArrayList<ForumMensagem>(0);

	@Transient
	private int totalTopicos;

	@Transient
	private Date dataUltimaMensagem;
	
	private Character nivel;
	
	public String getNivelDescricao() {
		return NivelEnsino.getDescricao(nivel);
	}

	public Forum() {
	}

	/**
	 * @return the data
	 */
	public Date getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(Date data) {
		this.data = data;
	}

	/**
	 * @return the descricao
	 */
	public String getDescricao() {
		return descricao;
	}

	/**
	 * @param descricao
	 *            the descricao to set
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	
	public Boolean getAtivo() { 
		return this.ativo; 
	}
	
	public void setAtivo(Boolean ativo) {
		this.ativo = ativo; 
	}
	
	
	/**
	 * @return the mensagens
	 */
	public List<ForumMensagem> getMensagens() {
		return mensagens;
	}

	/**
	 * @param mensagens
	 *            the mensagens to set
	 */
	public void setMensagens(List<ForumMensagem> mensagens) {
		this.mensagens = mensagens;
	}

	/**
	 * @return the titulo
	 */
	public String getTitulo() {
		return titulo;
	}

	/**
	 * @param titulo
	 *            the titulo to set
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/**
	 * @return the turma
	 */
	public Turma getTurma() {
		return turma;
	}

	/**
	 * @param turma
	 *            the turma to set
	 */
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/**
	 * @return the usuario
	 */
	public Usuario getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario
	 *            the usuario to set
	 */
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

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

	public Date getDataUltimaMensagem() {
		return dataUltimaMensagem;
	}

	/**
	 * @return the totalTopicos
	 */
	public int getTotalTopicos() {
		return totalTopicos;
	}

	/**
	 * @param totalTopicos
	 *            the totalTopicos to set
	 */
	public void setTotalTopicos(int totalTopicos) {
		this.totalTopicos = totalTopicos;
	}

	/**
	 * @param dataUltimaMensagem
	 *            the dataUltimaMensagem to set
	 */
	public void setDataUltimaMensagem(Date dataUltimaMensagem) {
		this.dataUltimaMensagem = dataUltimaMensagem;
	}

	public short getTipo() {
		return tipo;
	}

	public void setTipo(short tipo) {
		this.tipo = tipo;
	}

	/**
	 * @return the topicos
	 */
	public boolean isTopicos() {
		return topicos;
	}

	/**
	 * @param topicos the topicos to set
	 */
	public void setTopicos(boolean topicos) {
		this.topicos = topicos;
	}

	public boolean isMural() {
		return this.tipo == MURAL;
	}

	public Boolean getCurso() {
		return curso;
	}

	public void setCurso(Boolean curso) {
		this.curso = curso;
	}

	public int getIdCursoCoordenador() {
		return idCursoCoordenador;
	}

	public void setIdCursoCoordenador(int idCursoCoordenador) {
		this.idCursoCoordenador = idCursoCoordenador;
	}

	public String getMensagemAtividade() {
		return "Novo fórum cadastrado";
	}

	public Character getNivel() {
		return nivel;
	}

	public void setNivel(Character nivel) {
		this.nivel = nivel;
	}

	
}
