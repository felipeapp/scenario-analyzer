package br.ufrn.sigaa.ava.dominio;

import java.util.Date;

/**
 * Classe auxiliar que guarda os dados de uma aula para a geração da planilha de
 * frequência.
 * 
 * @author Fred_Castro
 * 
 */

public class AulaFrequencia implements Comparable<AulaFrequencia> {

	/** Dia da aula. */
	private int dia;
	/** Mês da aula. */
	private int mes;
	/** Número de aulas. */
	private int aulas;
	/** Número de aulas extra. */
	private int aulasExtra;
	/** Data da aula. */
	private Date data;
	/** Indica se a frequência referente a aula já foi lançada. */
	private boolean lancada;
	/** Indica se a data da aula cai em um feriado. */
	private boolean feriado;
	/** Indica se a {@link AulaFrequencia} faz referência a uma aula cancelada. */
	private boolean aulaCancelada;

	public int getDia() {
		return dia;
	}

	public void setDia(int dia) {
		this.dia = dia;
	}

	public int getMes() {
		return mes;
	}

	public void setMes(int mes) {
		this.mes = mes;
	}

	public int getAulas() {
		return aulas;
	}

	public void setAulas(int aulas) {
		this.aulas = aulas;
	}
	
	public int getAulasExtra() {
		return aulasExtra;
	}

	public void setAulasExtra(int aulasExtra) {
		this.aulasExtra = aulasExtra;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public boolean isLancada() {
		return lancada;
	}

	public void setLancada(boolean lancada) {
		this.lancada = lancada;
	}

	public boolean isFeriado() {
		return feriado;
	}

	public void setFeriado(boolean feriado) {
		this.feriado = feriado;
	}

	@Override
	public int compareTo(AulaFrequencia o) {
		return this.data.compareTo(o.getData());
	}

	public void setAulaCancelada(boolean aulaCancelada) {
		this.aulaCancelada = aulaCancelada;
	}

	public boolean isAulaCancelada() {
		return aulaCancelada;
	}
}
