/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/10/2012
 */
package fundacao.integracao.projetos;

import java.io.Serializable;

/**
 * DTO que cont�m os dados espec�ficos do cronograma de desembolso.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class CronogramaDesembolsoDTO implements Serializable {
	
	/** Representa o m�s do cronograma. */
	private Integer mes;

	/** Representa o ano do cronograma. */
	private Integer ano;

	/** Representa o valor do cronograma. */
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
