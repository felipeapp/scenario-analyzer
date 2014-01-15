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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que armazena dados sobre produções intelectuais publicadas por docentes em anais de eventos
 *
 * @author Gleydson
 */
@Entity
@Table(name = "publicacao_evento", schema = "prodocente")
@PrimaryKeyJoinColumn(name = "id_publicacao_evento")
public class PublicacaoEvento extends Publicacao {

	@Column(name = "autores")
	private String autores;

	@Column(name = "organizadores")
	private String organizadores;

	@Column(name = "nome_evento")
	private String nomeEvento;

	@Column(name = "pagina_inicial")
	private Integer paginaInicial;

	@Column(name = "destaque")
	private Boolean destaque;

	@Column(name = "pagina_final")
	private Integer paginaFinal;

	@Column(name = "apresentado")
	private Boolean apresentado;

	@Column(name = "natureza")
	private Character natureza;

	@JoinColumn(name = "id_tipo_evento", referencedColumnName = "id_tipo_evento")
	@ManyToOne
	private TipoEvento tipoEvento = new TipoEvento();

	@JoinColumn(name = "id_tipo_regiao", referencedColumnName = "id_tipo_regiao")
	@ManyToOne
	private TipoRegiao tipoRegiao = new TipoRegiao();

	/** Creates a new instance of PublicacaoEvento */
	public PublicacaoEvento() {
	}

	public Boolean getApresentado() {
		return apresentado;
	}

	public void setApresentado(Boolean apresentado) {
		this.apresentado = apresentado;
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

	public Character getNatureza() {
		return natureza;
	}

	@Transient
	public String getNaturezaDesc() {

		if (natureza != null) {
			switch ( natureza ) {
			case 'T':
				return "Trabalho Completo";
			case 'R':
				return "Resumo";
			case 'E':
				return "Resumo Expandido";
			case 'O':
				return "Outros";
			}
		}
		return "";
	}

	public void setNatureza(Character natureza) {
		this.natureza = natureza;
	}

	public String getNomeEvento() {
		return nomeEvento;
	}

	public void setNomeEvento(String nomeEvento) {
		this.nomeEvento = nomeEvento;
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

	public TipoEvento getTipoEvento() {
		return tipoEvento;
	}

	public void setTipoEvento(TipoEvento tipoEvento) {
		this.tipoEvento = tipoEvento;
	}

	public TipoRegiao getTipoRegiao() {
		return tipoRegiao;
	}

	public void setTipoRegiao(TipoRegiao tipoRegiao) {
		this.tipoRegiao = tipoRegiao;
	}

	/*
	 * Campos Obrigatórios: Titulo, Tipo Evento, Tipo Regiao, Participacao,
	 * Natureza, Local, Data
	 */

	@Override
	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getNomeEvento(), "Nome do Evento", lista);
		ValidatorUtil.validateRequired(getAutores(), "Autores", lista);
		ValidatorUtil.validateRequired(getTipoRegiao(), "Âmbito", lista);
		ValidatorUtil.validateRequired(getTipoEvento(), "Tipo de Evento", lista);
		ValidatorUtil.validateRequired(getNatureza(), "Natureza", lista);
		ValidatorUtil.validateRequired(getArea(), "Área", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-Área", lista);
		ValidatorUtil.validatePagina(
				(paginaInicial != null ? paginaInicial : 0),
				(paginaFinal != null ? paginaFinal : 0), lista);
		lista.addAll(super.validate().getMensagens());

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


	/* (non-Javadoc)
	 * @see br.ufrn.sigaa.prodocente.producao.dominio.Producao#getDescricaoCompleta()
	 */
	@Override
	public String getDescricaoCompleta() {
		return getTitulo() +
			(getNomeEvento() != null ? ", " + getNomeEvento() : "");
	}
}
