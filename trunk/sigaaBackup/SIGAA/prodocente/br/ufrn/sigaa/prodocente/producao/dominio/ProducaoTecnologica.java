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
 *
 * @author Gleydson
 */
@Entity
@Table(name = "producao_tecnologica", schema = "prodocente")
@PrimaryKeyJoinColumn(name = "id_producao_tecnologica")
public class ProducaoTecnologica extends Producao {

	@Column(name = "autores")
	private String autores;

	@JoinColumn(name = "id_tipo_producao_tecnologica", referencedColumnName = "id_tipo_producao_tecnologica")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoProducaoTecnologica tipoProducaoTecnologica = new TipoProducaoTecnologica();

	@Column(name = "local")
	private String localPublicacao;

	@Column(name = "informacao")
	private String informacao;

	@Column(name = "numero_docentes")
	private int numeroDocentes;

	@Column(name = "numero_docentes_outros")
	private int numeroDocentesOutros;

	@Column(name = "numero_estudantes")
	private int numeroEstudantes;

	@Column(name = "numero_outros")
	private int numeroOutros;

	@Column(name = "numero_tecnicos")
	private int numeroTecnicos;

	@Column(name = "premiada")
	private Boolean premiada;

	@JoinColumn(name = "id_tipo_regiao", referencedColumnName = "id_tipo_regiao")
	@ManyToOne(fetch = FetchType.EAGER)
	private TipoRegiao tipoRegiao = new TipoRegiao();


	/** Creates a new instance of ProducaoTecnologica */
	public ProducaoTecnologica() {
	}

	public String getAutores() {
		return autores;
	}

	public void setAutores(String autores) {
		this.autores = autores;
	}

	public Boolean getPremiada() {
		return premiada;
	}

	public void setPremiada(Boolean premiada) {
		this.premiada = premiada;
	}

	public TipoProducaoTecnologica getTipoProducaoTecnologica() {
		return tipoProducaoTecnologica;
	}

	public void setTipoProducaoTecnologica(
			TipoProducaoTecnologica tipoProducaoTecnologica) {
		this.tipoProducaoTecnologica = tipoProducaoTecnologica;
	}


	/*
	 * Campos Obrigatorios: Titulo, Tipo Producao Tecnologica, Participacao, Tipo Regiao,
	 * 						Local, Data
	 */

	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDataProducao(), "Data de Produção", lista);
		ValidatorUtil.validateRequired(getTitulo(),"Título", lista);
		ValidatorUtil.validateRequired(getTipoParticipacao(),"Tipo de Participação", lista);
		ValidatorUtil.validateRequired(getArea(), "Área", lista);
		ValidatorUtil.validateRequired(getSubArea(), "Sub-Área", lista);
		ValidatorUtil.validateRequired(getTipoRegiao(), "Âmbito", lista);
		ValidatorUtil.validateRequired(getAnoReferencia(), "Ano de Referência", lista);

		return lista;
	}

	public String getLocalPublicacao() {
		return localPublicacao;
	}

	public void setLocalPublicacao(String localPublicacao) {
		this.localPublicacao = localPublicacao;
	}

	public String getInformacao() {
		return informacao;
	}

	public void setInformacao(String informacao) {
		this.informacao = informacao;
	}

	public int getNumeroDocentes() {
		return numeroDocentes;
	}

	public void setNumeroDocentes(int numeroDocentes) {
		this.numeroDocentes = numeroDocentes;
	}

	public int getNumeroDocentesOutros() {
		return numeroDocentesOutros;
	}

	public void setNumeroDocentesOutros(int numeroDocentesOutros) {
		this.numeroDocentesOutros = numeroDocentesOutros;
	}

	public int getNumeroEstudantes() {
		return numeroEstudantes;
	}

	public void setNumeroEstudantes(int numeroEstudantes) {
		this.numeroEstudantes = numeroEstudantes;
	}

	public int getNumeroOutros() {
		return numeroOutros;
	}

	public void setNumeroOutros(int numeroOutros) {
		this.numeroOutros = numeroOutros;
	}

	public TipoRegiao getTipoRegiao() {
		return tipoRegiao;
	}

	public void setTipoRegiao(TipoRegiao tipoRegiao) {
		this.tipoRegiao = tipoRegiao;
	}

	public int getNumeroTecnicos() {
		return numeroTecnicos;
	}

	public void setNumeroTecnicos(int numeroTecnicos) {
		this.numeroTecnicos = numeroTecnicos;
	}


}
