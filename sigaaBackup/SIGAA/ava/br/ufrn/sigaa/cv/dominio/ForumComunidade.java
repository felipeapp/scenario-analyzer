/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 12/12/2006
 *
 */
package br.ufrn.sigaa.cv.dominio;

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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Representa um Forum de discussão em uma determinada turma
 * 
 * @author Gleydson Lima
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "forum", schema = "cv")
@HumanName(value = "Fórum", genero = 'M')
public class ForumComunidade implements DominioComunidadeVirtual {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_forum", nullable = false)
	private int id;

	private Boolean ativo;

	private String descricao;

	private String titulo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_comunidade")
	private ComunidadeVirtual comunidade;

	@CriadoPor
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;

	@CriadoEm
	@Column(name = "data_criacao")
	private Date data;

	@OneToMany(mappedBy = "forum", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<ForumMensagemComunidade> mensagens = new ArrayList<ForumMensagemComunidade>(0);

	@Transient
	private int totalTopicos;

	@Transient
	private Date dataUltimaMensagem;

	public ForumComunidade() {
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
	public List<ForumMensagemComunidade> getMensagens() {
		return mensagens;
	}

	/**
	 * @param mensagens
	 *            the mensagens to set
	 */
	public void setMensagens(List<ForumMensagemComunidade> mensagens) {
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

	/**
	 * Valida os campos do próprio objeto
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
		if (descricao != null && !"".equals(descricao.trim())
				&& descricao.length() < 3)
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

	public ComunidadeVirtual getComunidade() {
		return comunidade;
	}

	public void setComunidade(ComunidadeVirtual comunidade) {
		this.comunidade = comunidade;
	}

	public void setDataUltimaMensagem(Date dataUltimaMensagem) {
		this.dataUltimaMensagem = dataUltimaMensagem;
	}

	public String getMensagemAtividade() {
		return null;
	}

}