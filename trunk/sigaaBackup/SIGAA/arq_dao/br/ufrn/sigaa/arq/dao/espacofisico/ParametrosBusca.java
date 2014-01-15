/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '10/12/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.espacofisico;

import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.espacofisico.dominio.TipoRecursoEspacoFisico;

/**
 * Objeto que encapsula os parâmetros passados pelo formulário de busca por espaço físico
 * 
 * @author Henrique André
 *
 */
public class ParametrosBusca {

	private String codigo;

	private Integer capacidadeInicio, capacidadeFim;

	private String descricao;

	private Double areaInicio, areaFim;

	private TipoRecursoEspacoFisico tipoRecurso = new TipoRecursoEspacoFisico();
	
	private Unidade localizacao = new Unidade();
	
	private Unidade reservaPrioritaria = new Unidade();
	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Integer getCapacidadeInicio() {
		return capacidadeInicio;
	}

	public void setCapacidadeInicio(Integer capacidadeInicio) {
		this.capacidadeInicio = capacidadeInicio;
	}

	public Integer getCapacidadeFim() {
		return capacidadeFim;
	}

	public void setCapacidadeFim(Integer capacidadeFim) {
		this.capacidadeFim = capacidadeFim;
	}

	public Unidade getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(Unidade localizacao) {
		this.localizacao = localizacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Double getAreaInicio() {
		return areaInicio;
	}

	public void setAreaInicio(Double areaInicio) {
		this.areaInicio = areaInicio;
	}

	public Double getAreaFim() {
		return areaFim;
	}

	public void setAreaFim(Double areaFim) {
		this.areaFim = areaFim;
	}

	public TipoRecursoEspacoFisico getTipoRecurso() {
		return tipoRecurso;
	}

	public void setTipoRecurso(TipoRecursoEspacoFisico tipoRecurso) {
		this.tipoRecurso = tipoRecurso;
	}

	public Unidade getReservaPrioritaria() {
		return reservaPrioritaria;
	}

	public void setReservaPrioritaria(Unidade reservaPrioritaria) {
		this.reservaPrioritaria = reservaPrioritaria;
	}

}
