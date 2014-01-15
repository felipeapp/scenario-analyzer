/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Data Transfer Object para informações de responsáveis de unidades.
 * 
 * @author David Pereira
 *
 */
public class ResponsavelDTO implements Serializable {

	private int id;
	
	private int idUnidade;
	
	private int idServidor;
	
	private int idCargo;
	
	private Date inicio;
	
	private Date fim;
	
	private Character nivelResponsabilidade;
	
	private Character origem;
	
	private String observacao;
	
	private Integer idDesignacao;

	/**
	 * Sistema de origem que está utilizando o serviço.
	 */
	private int sistemaRequisitor;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIdUnidade() {
		return idUnidade;
	}

	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}

	public int getIdServidor() {
		return idServidor;
	}

	public void setIdServidor(int idServidor) {
		this.idServidor = idServidor;
	}

	public int getIdCargo() {
		return idCargo;
	}

	public void setIdCargo(int idCargo) {
		this.idCargo = idCargo;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public Character getNivelResponsabilidade() {
		return nivelResponsabilidade;
	}

	public void setNivelResponsabilidade(Character nivelResponsabilidade) {
		this.nivelResponsabilidade = nivelResponsabilidade;
	}

	public Character getOrigem() {
		return origem;
	}

	public void setOrigem(Character origem) {
		this.origem = origem;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Integer getIdDesignacao() {
		return idDesignacao;
	}

	public void setIdDesignacao(Integer idDesignacao) {
		this.idDesignacao = idDesignacao;
	}

	public int getSistemaRequisitor() {
		return sistemaRequisitor;
	}

	public void setSistemaRequisitor(int sistemaRequisitor) {
		this.sistemaRequisitor = sistemaRequisitor;
	}
	
}
