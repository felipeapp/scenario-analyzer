/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/04/2012
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * Data Transfer Object para informações do cronograma de desembolso.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class CronogramaDesembolsoDTO implements Serializable {
	
	/** Mês do cronograma. */
	private Integer mes;

	/** Ano do cronograma. */
	private Integer ano;

	/** Valor do cronograma. */
	private Double valor;

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getMes() {
		return mes;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getAno() {
		return ano;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Double getValor() {
		return valor;
	}

}
