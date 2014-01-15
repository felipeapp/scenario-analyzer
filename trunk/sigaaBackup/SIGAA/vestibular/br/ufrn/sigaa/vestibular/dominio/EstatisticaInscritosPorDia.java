/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 26/07/2010
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import java.util.Date;

/**
 * Classe utilizada para gerar relat�rios estat�sticos referentes � inscri��o de
 * candidatos no vestibular.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
public class EstatisticaInscritosPorDia {

	/** Data ao qual se refere a estat�tisca. */
	private Date data;
	/** N�mero de candidatos inscritos no primeiro quarto de hora do dia (das 00h00min �s 05h59min). */
	private int primeiroQuartoDia;
	/** N�mero de candidatos inscritos no segundo quarto de hora do dia (das 06h00min �s 11h59min). */
	private int segundoQuartoDia;
	/** N�mero de candidatos inscritos no terceiro quarto de hora do dia (das 12h00min �s 17h59min). */
	private int terceiroQuartoDia;
	/** N�mero de candidatos inscritos no quarto quarto de hora do dia (das 18h00min �s 23h59min). */
	private int quartoQuartoDia;
	/** N�mero de candidatos inscritos at� as 18 horas do dia. */
	private int totalAte18Horas;
	/** N�mero de candidatos inscri��es realizadas. (Um candidato pode realizar mais de uma inscri��o). */
	private int totalInscricoes;
	/** N�mero de candidatos inscritos. */
	private int totalCandidatos;
	/** N�mero de candidatos isentos inscritos. */
	private int totalCandidatosIsentos;
	/** N�mero da Semana */
	private String numeroSemana;
	
	/** Construtor padr�o. */
	public EstatisticaInscritosPorDia() {
	}
	
	/** Retorna a data ao qual se refere a estat�tisca. 
	 * @return
	 */
	public Date getData() {
		return data;
	}

	/** Seta a data ao qual se refere a estat�tisca.
	 * @param data
	 */
	public void setData(Date data) {
		this.data = data;
	}

	/** Retorna o n�mero de candidatos inscritos no primeiro quarto de hora do dia (das 00h00min �s 05h59min). 
	 * @return
	 */
	public int getPrimeiroQuartoDia() {
		return primeiroQuartoDia;
	}

	/** Seta o n�mero de candidatos inscritos no primeiro quarto de hora do dia (das 00h00min �s 05h59min).
	 * @param primeiroQuartoDia
	 */
	public void setPrimeiroQuartoDia(int primeiroQuartoDia) {
		this.primeiroQuartoDia = primeiroQuartoDia;
	}

	/** Retorna o n�mero de candidatos inscritos no segundo quarto de hora do dia (das 06h00min �s 11h59min). 
	 * @return
	 */
	public int getSegundoQuartoDia() {
		return segundoQuartoDia;
	}

	/** Seta o n�mero de candidatos inscritos no segundo quarto de hora do dia (das 06h00min �s 11h59min).
	 * @param segundoQuartoDia
	 */
	public void setSegundoQuartoDia(int segundoQuartoDia) {
		this.segundoQuartoDia = segundoQuartoDia;
	}

	/** Retorna o n�mero de candidatos inscritos no terceiro quarto de hora do dia (das 12h00min �s 17h59min). 
	 * @return
	 */
	public int getTerceiroQuartoDia() {
		return terceiroQuartoDia;
	}

	/** Seta o n�mero de candidatos inscritos no terceiro quarto de hora do dia (das 12h00min �s 17h59min).
	 * @param terceiroQuartoDia
	 */
	public void setTerceiroQuartoDia(int terceiroQuartoDia) {
		this.terceiroQuartoDia = terceiroQuartoDia;
	}

	/** Retorna o n�mero de candidatos inscritos no quarto quarto de hora do dia (das 18h00min �s 23h59min). 
	 * @return
	 */
	public int getQuartoQuartoDia() {
		return quartoQuartoDia;
	}

	/** Seta o n�mero de candidatos inscritos no quarto quarto de hora do dia (das 18h00min �s 23h59min).
	 * @param quartoQuartoDia
	 */
	public void setQuartoQuartoDia(int quartoQuartoDia) {
		this.quartoQuartoDia = quartoQuartoDia;
	}

	/** Retorna o n�mero de candidatos inscritos at� as 18 horas do dia. 
	 * @return
	 */
	public int getTotalAte18Horas() {
		return totalAte18Horas;
	}

	/** Seta o n�mero de candidatos inscritos at� as 18 horas do dia.
	 * @param totalAte18Horas
	 */
	public void setTotalAte18Horas(int totalAte18Horas) {
		this.totalAte18Horas = totalAte18Horas;
	}

	/** Retorna o n�mero de candidatos inscri��es realizadas. (Um candidato pode realizar mais de uma inscri��o). 
	 * @return
	 */
	public int getTotalInscricoes() {
		return totalInscricoes;
	}

	/** Seta o n�mero de candidatos inscri��es realizadas. (Um candidato pode realizar mais de uma inscri��o).
	 * @param totalInscricoes
	 */
	public void setTotalInscricoes(int totalInscricoes) {
		this.totalInscricoes = totalInscricoes;
	}

	/** Retorna o n�mero de candidatos inscritos. 
	 * @return
	 */
	public int getTotalCandidatos() {
		return totalCandidatos;
	}

	/** Seta o n�mero de candidatos inscritos.
	 * @param totalCandidatos
	 */
	public void setTotalCandidatos(int totalCandidatos) {
		this.totalCandidatos = totalCandidatos;
	}

	/** Retorna o n�mero de candidatos isentos inscritos. 
	 * @return
	 */
	public int getTotalCandidatosIsentos() {
		return totalCandidatosIsentos;
	}

	/** Seta o n�mero de candidatos isentos inscritos.
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