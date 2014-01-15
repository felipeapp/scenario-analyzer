/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/04/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.relatorios;

/**
 * Classe auxiliar utilizada para a geração de relatórios sintéticos de entradas por ano
 * 
 * @author Leonardo
 *
 */
public class LinhaRelatorioSinteticoEntradas {

	private int ano;
	
	private long entradas;
	
	public LinhaRelatorioSinteticoEntradas(){
		
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public long getEntradas() {
		return entradas;
	}

	public void setEntradas(long entradas) {
		this.entradas = entradas;
	}
	
	
}
