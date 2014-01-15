/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '24/11/2006'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

/**
 * 
 * 
 * @author Gleydson
 *
 */
public class AbstractAvaliacaoDocente {

	private int anoVigencia;

	private Double totalPontos;

	public int getAnoVigencia() {
		return anoVigencia;
	}

	public void setAnoVigencia(int anoVigencia) {
		this.anoVigencia = anoVigencia;
	}

	public Double getTotalPontos() {
		return totalPontos;
	}

	public void setTotalPontos(Double totalPontos) {
		this.totalPontos = totalPontos;
	}


}
