/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 26/07/2010
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import java.util.Date;

/**
 * Classe utilizada para gerar relatórios estatísticos referentes à inscrição de
 * candidatos no vestibular.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
public class EstatisticaInscritosPorDia {

	/** Data ao qual se refere a estatítisca. */
	private Date data;
	/** Número de candidatos inscritos no primeiro quarto de hora do dia (das 00h00min às 05h59min). */
	private int primeiroQuartoDia;
	/** Número de candidatos inscritos no segundo quarto de hora do dia (das 06h00min às 11h59min). */
	private int segundoQuartoDia;
	/** Número de candidatos inscritos no terceiro quarto de hora do dia (das 12h00min às 17h59min). */
	private int terceiroQuartoDia;
	/** Número de candidatos inscritos no quarto quarto de hora do dia (das 18h00min às 23h59min). */
	private int quartoQuartoDia;
	/** Número de candidatos inscritos até as 18 horas do dia. */
	private int totalAte18Horas;
	/** Número de candidatos inscrições realizadas. (Um candidato pode realizar mais de uma inscrição). */
	private int totalInscricoes;
	/** Número de candidatos inscritos. */
	private int totalCandidatos;
	/** Número de candidatos isentos inscritos. */
	private int totalCandidatosIsentos;
	/** Número da Semana */
	private String numeroSemana;
	
	/** Construtor padrão. */
	public EstatisticaInscritosPorDia() {
	}
	
	/** Retorna a data ao qual se refere a estatítisca. 
	 * @return
	 */
	public Date getData() {
		return data;
	}

	/** Seta a data ao qual se refere a estatítisca.
	 * @param data
	 */
	public void setData(Date data) {
		this.data = data;
	}

	/** Retorna o número de candidatos inscritos no primeiro quarto de hora do dia (das 00h00min às 05h59min). 
	 * @return
	 */
	public int getPrimeiroQuartoDia() {
		return primeiroQuartoDia;
	}

	/** Seta o número de candidatos inscritos no primeiro quarto de hora do dia (das 00h00min às 05h59min).
	 * @param primeiroQuartoDia
	 */
	public void setPrimeiroQuartoDia(int primeiroQuartoDia) {
		this.primeiroQuartoDia = primeiroQuartoDia;
	}

	/** Retorna o número de candidatos inscritos no segundo quarto de hora do dia (das 06h00min às 11h59min). 
	 * @return
	 */
	public int getSegundoQuartoDia() {
		return segundoQuartoDia;
	}

	/** Seta o número de candidatos inscritos no segundo quarto de hora do dia (das 06h00min às 11h59min).
	 * @param segundoQuartoDia
	 */
	public void setSegundoQuartoDia(int segundoQuartoDia) {
		this.segundoQuartoDia = segundoQuartoDia;
	}

	/** Retorna o número de candidatos inscritos no terceiro quarto de hora do dia (das 12h00min às 17h59min). 
	 * @return
	 */
	public int getTerceiroQuartoDia() {
		return terceiroQuartoDia;
	}

	/** Seta o número de candidatos inscritos no terceiro quarto de hora do dia (das 12h00min às 17h59min).
	 * @param terceiroQuartoDia
	 */
	public void setTerceiroQuartoDia(int terceiroQuartoDia) {
		this.terceiroQuartoDia = terceiroQuartoDia;
	}

	/** Retorna o número de candidatos inscritos no quarto quarto de hora do dia (das 18h00min às 23h59min). 
	 * @return
	 */
	public int getQuartoQuartoDia() {
		return quartoQuartoDia;
	}

	/** Seta o número de candidatos inscritos no quarto quarto de hora do dia (das 18h00min às 23h59min).
	 * @param quartoQuartoDia
	 */
	public void setQuartoQuartoDia(int quartoQuartoDia) {
		this.quartoQuartoDia = quartoQuartoDia;
	}

	/** Retorna o número de candidatos inscritos até as 18 horas do dia. 
	 * @return
	 */
	public int getTotalAte18Horas() {
		return totalAte18Horas;
	}

	/** Seta o número de candidatos inscritos até as 18 horas do dia.
	 * @param totalAte18Horas
	 */
	public void setTotalAte18Horas(int totalAte18Horas) {
		this.totalAte18Horas = totalAte18Horas;
	}

	/** Retorna o número de candidatos inscrições realizadas. (Um candidato pode realizar mais de uma inscrição). 
	 * @return
	 */
	public int getTotalInscricoes() {
		return totalInscricoes;
	}

	/** Seta o número de candidatos inscrições realizadas. (Um candidato pode realizar mais de uma inscrição).
	 * @param totalInscricoes
	 */
	public void setTotalInscricoes(int totalInscricoes) {
		this.totalInscricoes = totalInscricoes;
	}

	/** Retorna o número de candidatos inscritos. 
	 * @return
	 */
	public int getTotalCandidatos() {
		return totalCandidatos;
	}

	/** Seta o número de candidatos inscritos.
	 * @param totalCandidatos
	 */
	public void setTotalCandidatos(int totalCandidatos) {
		this.totalCandidatos = totalCandidatos;
	}

	/** Retorna o número de candidatos isentos inscritos. 
	 * @return
	 */
	public int getTotalCandidatosIsentos() {
		return totalCandidatosIsentos;
	}

	/** Seta o número de candidatos isentos inscritos.
	 * @param totalCandidatosIsentos
	 */
	public void setTotalCandidatosIsentos(int totalCandidatosIsentos) {
		this.totalCandidatosIsentos = totalCandidatosIsentos;
	}

	public String getNumeroSemana() {
		return numeroSemana;
	}

	public void setNumeroSemana(String numeroSemana) {
		this.numeroSemana = numeroSemana;
	}

}