/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Capítulo de livro publicado por um servidor da instituição
 *
 * @author Gleydson
 */
@Entity
@Table(name = "capitulo", schema = "prodocente")
@PrimaryKeyJoinColumn(name = "id_capitulo")
public class Capitulo extends Publicacao {

	@Column(name = "capitulos_livro")
	private String capitulosLivro;

	@Column(name = "autores")
	private String autores;

	@Column(name = "titulo_livro")
	private String tituloLivro;

	@Column(name = "editor")
	private String editor;

	@Column(name = "pagina_inicial")
	private Integer paginaInicial;

	@Column(name = "premiada")
	private Boolean premiada;

	@Column(name = "pagina_final")
	private Integer paginaFinal;

	@JoinColumn(name = "id_tipo_regiao", referencedColumnName = "id_tipo_regiao")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoRegiao tipoRegiao = new TipoRegiao();

	/*
	 * Campos Obrigatórios: Titulo, Tipo Regiao, Participacao, Data,
	 * 						Titulo Livro, Editor
	 */

	/** Creates a new instance of Capitulo */
	public Capitulo() {
	}

	public Capitulo(Integer idCapitulo) {
		setId(idCapitulo);
	}

	public String getAutores() {

		return autores;
	}

	public void setAutores(String autores) {
		this.autores = autores;
	}

	public String getCapitulosLivro() {
		return capitulosLivro;
	}

	public void setCapitulosLivro(String capitulosLivro) {
		this.capitulosLivro = capitulosLivro;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public Integer getPaginaFinal() {
		return paginaFinal;
	}

	public void setPaginaFinal(Integer paginaFinal) {
		this.paginaFinal = paginaFinal;
	}

	public Integer getPaginaInicial() {
		return paginaInicial;
	}

	public void setPaginaInicial(Integer paginaInicial) {
		this.paginaInicial = paginaInicial;
	}

	public Boolean getPremiada() {
		return premiada;
	}

	public void setPremiada(Boolean premiada) {
		this.premiada = premiada;
	}

	public TipoRegiao getTipoRegiao() {
		return tipoRegiao;
	}

	public void setTipoRegiao(TipoRegiao tipoRegiao) {
		this.tipoRegiao = tipoRegiao;
	}

	public String getTituloLivro() {
		return tituloLivro;
	}

	public void setTituloLivro(String tituloLivro) {
		this.tituloLivro = tituloLivro;
	}


	/*
	 * Campos Obrigatórios: Titulo, Tipo Regiao, Participacao, Data,
	 * 						Titulo Livro, Editor
	 */

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getTitulo(),"Título do Capítulo", lista);
		ValidatorUtil.validateRequired(getAutores(), "Autores", lista);
		ValidatorUtil.validateRequired(getTituloLivro(), "Título ", lista);
		ValidatorUtil.validateRequired(getEditor(), "Editor", lista);
		ValidatorUtil.validateRequired(getLocalPublicacao(), "Local de Publicação", lista);
		ValidatorUtil.validateRequired(getDataProducao(),"Data de Produção", lista);
		ValidatorUtil.validateRequired(getAnoReferencia(), "Ano de Referência", lista);
		ValidatorUtil.validateRequired(getArea(), "Área", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-Área", lista);
		ValidatorUtil.validateRequired(getTipoRegiao(),"Publicação", lista);
		ValidatorUtil.validateRequired(getTipoParticipacao(),"Tipo de Participação", lista);
		ValidatorUtil.validatePagina((paginaInicial != null ? paginaInicial : 0), (paginaFinal != null ? paginaFinal : 0), lista);
		lista.addAll(super.validate().getMensagens());

		return lista;
	}

}
