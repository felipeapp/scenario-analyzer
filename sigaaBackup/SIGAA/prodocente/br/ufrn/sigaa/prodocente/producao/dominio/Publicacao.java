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
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Entidade que contém as informações compartilhadas por todas as produções intelectuais referentes a publicações
 *
 * @author Gleydson
 */
@Entity
@Table(name = "producao_publicacao", schema = "prodocente")
@PrimaryKeyJoinColumn(name = "id_producao_publicacao")
public abstract class Publicacao extends Producao {

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

	@Column(name = "data_publicacao")
	@Temporal(TemporalType.DATE)
	private Date dataPublicacao;

	@Column(name = "local_publicacao")
	private String localPublicacao;

	public Date getDataPublicacao() {
		return dataPublicacao;
	}

	public void setDataPublicacao(Date dataPublicacao) {
		setAnoReferencia(CalendarUtils.getAno(dataPublicacao));
		this.dataPublicacao = dataPublicacao;
	}

	public String getLocalPublicacao() {
		return localPublicacao;
	}

	public void setLocalPublicacao(String localPublicacao) {
		this.localPublicacao = localPublicacao;
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

	/*
	 * Campos Obrigatorios: Titulo, Data, Local
	 */

	@Override
	public ListaMensagens validate() {

		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(getDataProducao(), "Data Produção", lista);
		ValidatorUtil.validateRequired(getTitulo(),"Título", lista);
		ValidatorUtil.validateRequired(getAnoReferencia(), "Ano de Referência", lista);
		ValidatorUtil.validateRequired(getLocalPublicacao(), "Local de Publicação", lista);
		ValidatorUtil.validateRequired(getTipoParticipacao(),"Tipo de Participação", lista);

		return lista;
	}


}
