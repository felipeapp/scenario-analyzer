/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que registra os dados gerais de todas as produções artísticas, literárias ou visuais de docentes da instituição
 *
 * @author Gleydson
 */
@Entity
@Table(name = "producao_artistica_literaria_visual", schema = "prodocente")
@PrimaryKeyJoinColumn(name = "id_producao_artistica_literaria_visual")
public class ProducaoArtisticaLiterariaVisual extends Producao {

	@Column(name="premiada")
	private Boolean premiada;

	@Column(name = "autores")
	private String autores;

	@Column(name = "numero_docentes")
	private Integer numeroDocentes;

	@Column(name = "numero_docentes_outros")
	private Integer numeroDocentesOutros;

	@Column(name = "numero_estudantes")
	private Integer numeroEstudantes;

	@Column(name = "numero_tecnicos")
	private Integer numeroTecnicos;

	@Column(name = "numero_outros")
	private Integer numeroOutros;

	@Column(name = "periodo_inicio")
	@Temporal(TemporalType.DATE)
	private Date periodoInicio;

	@Column(name = "periodo_fim")
	@Temporal(TemporalType.DATE)
	private Date periodoFim;

	@JoinColumn(name = "id_tipo_artistico", referencedColumnName = "id_tipo_artistico")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoArtistico tipoArtistico = new TipoArtistico();

	@JoinColumn(name = "id_sub_tipo_artistico", referencedColumnName = "id_sub_tipo_artistico")
	@ManyToOne(fetch = FetchType.EAGER)
	private SubTipoArtistico subTipoArtistico = new SubTipoArtistico();

	@JoinColumn(name = "id_tipo_regiao", referencedColumnName = "id_tipo_regiao")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoRegiao tipoRegiao = new TipoRegiao();

	private String local;

	/** Creates a new instance of ProducaoArtisticaLiterariaVisual */
	public ProducaoArtisticaLiterariaVisual() {
	}


	public Boolean getPremiada()
	{
		return this.premiada;
	}

	public void setPremiada(Boolean premiada)
	{
		this.premiada = premiada;
	}


	public String getAutores() {
		return autores;
	}

	public void setAutores(String autores) {
		this.autores = autores;
	}

	public Integer getNumeroDocentes() {
		return numeroDocentes;
	}

	public void setNumeroDocentes(Integer numeroDocentes) {
		this.numeroDocentes = numeroDocentes;
	}

	public Integer getNumeroDocentesOutros() {
		return numeroDocentesOutros;
	}

	public void setNumeroDocentesOutros(Integer numeroDocentesOutros) {
		this.numeroDocentesOutros = numeroDocentesOutros;
	}

	public Integer getNumeroEstudantes() {
		return numeroEstudantes;
	}

	public void setNumeroEstudantes(Integer numeroEstudantes) {
		this.numeroEstudantes = numeroEstudantes;
	}

	public Integer getNumeroOutros() {
		return numeroOutros;
	}

	public void setNumeroOutros(Integer numeroOutros) {
		this.numeroOutros = numeroOutros;
	}

	public Integer getNumeroTecnicos() {
		return numeroTecnicos;
	}

	public void setNumeroTecnicos(Integer numeroTecnicos) {
		this.numeroTecnicos = numeroTecnicos;
	}

	public Date getPeriodoFim() {
		return periodoFim;
	}

	public void setPeriodoFim(Date periodoFim) {
		this.periodoFim = periodoFim;
	}

	public Date getPeriodoInicio() {
		return periodoInicio;
	}

	public void setPeriodoInicio(Date periodoInicio) {
		this.periodoInicio = periodoInicio;
	}

	public TipoArtistico getTipoArtistico() {
		return tipoArtistico;
	}

	public void setTipoArtistico(TipoArtistico tipoArtistico) {
		this.tipoArtistico = tipoArtistico;
	}

	public TipoRegiao getTipoRegiao() {
		return tipoRegiao;
	}

	public void setTipoRegiao(TipoRegiao tipoRegiao) {
		this.tipoRegiao = tipoRegiao;
	}

	public SubTipoArtistico getSubTipoArtistico() {
		return subTipoArtistico;
	}

	public void setSubTipoArtistico(SubTipoArtistico subTipoArtistico) {
		this.subTipoArtistico = subTipoArtistico;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	/*
	 * Campos Obrigatorios: Titulo, Tipo Regiao
	 */

	@Override
	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getTitulo(),"Título", lista);
		ValidatorUtil.validateRequired(getLocal(), "Local", lista);
		ValidatorUtil.validateRequired(getAutores(), "Autores", lista);
		ValidatorUtil.validateRequired(getTipoRegiao(), "Âmbito", lista);
		ValidatorUtil.validateRequired(getArea(), "Área", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-Área", lista);
		ValidatorUtil.validateRequired(getTipoArtistico(), "Tipo Artístico", lista);
		ValidatorUtil.validateRequired(getSubTipoArtistico(), "Sub-Tipo Artístico", lista);
		ValidatorUtil.validateRequired(getDataProducao(), "Data de Produção", lista);
		ValidatorUtil.validateRequired(getAnoReferencia(), "Ano de Referência", lista);
		ValidatorUtil.validateRequired(getPeriodoInicio(), "Data Inicial", lista);
		ValidatorUtil.validateRequired(getPeriodoFim(), "Data Final", lista);
		return lista;
	}
}
