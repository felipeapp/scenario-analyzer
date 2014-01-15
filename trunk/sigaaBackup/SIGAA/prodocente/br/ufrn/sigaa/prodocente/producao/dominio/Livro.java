/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que registra as informa��es de livros publicados por docentes da institui��o
 *
 * @author Gleydson
 */
@Entity
@Table(name = "livro", schema = "prodocente")
@PrimaryKeyJoinColumn(name = "id_livro")
public class Livro extends Publicacao {

	@Column(name = "editora")
	private String editora;

	@Column(name = "autores")
	private String autores;

	@Column(name = "organizadores")
	private String organizadores;

	@Column(name="quantidade_paginas")
	private Integer quantidadePaginas;

	@Column(name = "destaque")
	private Boolean destaque;

	@JoinColumn(name = "id_tipo_regiao", referencedColumnName = "id_tipo_regiao")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoRegiao tipoRegiao = new TipoRegiao();

	/** Creates a new instance of Livro */
	public Livro() {
	}

	public Livro(Integer idLivro) {
		setId(idLivro);
	}

	public String getAutores() {
		return autores;
	}

	public void setAutores(String autores) {
		this.autores = autores;
	}

	public Boolean getDestaque() {
		return destaque;
	}

	public void setDestaque(Boolean destaque) {
		this.destaque = destaque;
	}

	public String getEditora() {
		return editora;
	}

	public void setEditora(String editora) {
		this.editora = editora;
	}

	public Integer getQuantidadePaginas()
	{
		return this.quantidadePaginas;
	}
	public void setQuantidadePaginas(Integer qtdpaginas)
	{
		this.quantidadePaginas = qtdpaginas;
	}

	public TipoRegiao getTipoRegiao() {
		return tipoRegiao;
	}

	public void setTipoRegiao(TipoRegiao tipoRegiao) {
		this.tipoRegiao = tipoRegiao;
	}

	/*
	 * Campos Obrigat�rios: Titulo, Tipo Regiao, Participacao, Local, Editor, Data
	 */

	@Override
	public ListaMensagens validate(){
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getTitulo(),"T�tulo", lista);
		ValidatorUtil.validateRequired(getAutores(),"Autores", lista);
		ValidatorUtil.validateRequired(getLocalPublicacao(),"Local de Publica��o", lista);
		ValidatorUtil.validateRequired(getEditora(),"Editora", lista);
		ValidatorUtil.validateRequired(getTipoRegiao(),"Publica��o", lista);
		ValidatorUtil.validateRequired(getTipoParticipacao(),"Tipo de Participa��o", lista);
		ValidatorUtil.validateRequired(getArea(), "�rea", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-�rea", lista);
		ValidatorUtil.validateRequired(getDataProducao(), "Data de Produ��o", lista);
//		lista.addAll(super.validate().getMensagens());
		return lista;
	}

	/**
	 * @return the organizadores
	 */
	public String getOrganizadores() {
		return organizadores;
	}

	/**
	 * @param organizadores the organizadores to set
	 */
	public void setOrganizadores(String organizadores) {
		this.organizadores = organizadores;
	}

}
