/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/07/2008
 *
 */

package br.ufrn.sigaa.pesquisa.relatorios;


import br.ufrn.arq.util.CalendarUtils;

/**
 * Classe auxiliar utilizada na geração de relatório quantitativo de pesquisa.
 * Representa um 'mês/ano' ordenável.
 * 
 * @author leonardo
 *
 */
public class MesAno implements Comparable<MesAno>{

	private Integer mes, ano;
	
	public MesAno() {
	}
	
	public MesAno(Integer mes, Integer ano){
		this.mes = mes;
		this.ano = ano;
	}
	
	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public int compareTo(MesAno o) {
		int comparacao = ano.compareTo(o.getAno());
		return comparacao == 0 ? mes.compareTo(o.getMes()) : comparacao;
	}
	
	@Override
	public String toString() {
		return CalendarUtils.getMesAbreviado(mes) + "/" + ano;
	}
	
	public String getMesDesc(){
		return CalendarUtils.getMesAbreviado(mes);
	}
}
