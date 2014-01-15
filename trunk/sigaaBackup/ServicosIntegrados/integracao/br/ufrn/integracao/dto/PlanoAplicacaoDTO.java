/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/04/2012
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Data Transfer Object para informações dos planos de aplicação.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class PlanoAplicacaoDTO implements Serializable {

	/** Representa o elemento de despesa.*/
	private String elementoDespesa;

	/** Representa o valor de despesa.*/
	private Double valor;

	/** Representa as planilhas orçamentárias que detalham o plano de aplicação.*/
	private List<PlanilhaOrcamentariaDTO> planilhasOrcamentaria;

	public void setElementoDespesa(String elementoDespesa) {
		this.elementoDespesa = elementoDespesa;
	}

	public String getElementoDespesa() {
		return elementoDespesa;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Double getValor() {
		return valor;
	}

	public void setPlanilhasOrcamentaria(List<PlanilhaOrcamentariaDTO> planilhasOrcamentaria) {
		this.planilhasOrcamentaria = planilhasOrcamentaria;
	}

	public List<PlanilhaOrcamentariaDTO> getPlanilhasOrcamentaria() {
		return planilhasOrcamentaria;
	}
	
	
}
