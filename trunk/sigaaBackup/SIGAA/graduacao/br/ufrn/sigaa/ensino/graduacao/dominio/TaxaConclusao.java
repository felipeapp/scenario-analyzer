/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 07/10/2009
 *
 */	
package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.io.Serializable;

/**
 * A taxa de conclus�o dos cursos de gradua��o � um indicador calculado
 * anualmente por emio da raz�o entre os diplomados e ingressos. O valor de TCG
 * n�o expessa diretamente as taxas de sucesso observadas nos cursos da
 * universidade, ainda que haja uma rela��o estreita com fen�menos de reten��o e
 * evas�o. Na veradde TCG tamb�m contempla a efici�ncia com que a universidade
 * preenche as suas vagas ociosas decorrentes do abandono dos cursos.
 * 
 * TCG = DIP / ING5.
 * 
 * DIP = Diplomados ING5 = Ingressantes de 5 anos atr�s.
 * 
 * @author Gleydson Lima
 * 
 */
public class TaxaConclusao implements Serializable{

	private int ano;

	private int semestre;

	private int ingressantes;

	private int concluintes;
	
	private int anoIngresso;
	
	private int semestreIngresso;
	
	private int ingressantesAnual;
	
	private int concluintesAnual;
	
	private float taxaAnual;
	
	private int linhas = 1;

	public float getTaxaSucesso() {
		if (ingressantes == 0)
			return 0;
		else {
		    return (((float)concluintes / (float)ingressantes));
		}
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getSemestre() {
		return semestre;
	}

	public void setSemestre(int semestre) {
		this.semestre = semestre;
	}

	public int getIngressantes() {
		return ingressantes;
	}

	public void setIngressantes(int ingressantes) {
		this.ingressantes = ingressantes;
	}

	public int getConcluintes() {
		return concluintes;
	}

	public void setConcluintes(int concluintes) {
		this.concluintes = concluintes;
	}

	public int getAnoIngresso() {
		return anoIngresso;
	}

	public void setAnoIngresso(int anoIngresso) {
		this.anoIngresso = anoIngresso;
	}

	public int getSemestreIngresso() {
		return semestreIngresso;
	}

	public void setSemestreIngresso(int semestreIngresso) {
		this.semestreIngresso = semestreIngresso;
	}

	public int getLinhas() {
		return linhas;
	}

	public void setLinhas(int linhas) {
		this.linhas = linhas;
	}

	public float getTaxaAnual() {
		return taxaAnual;
	}

	public void setTaxaAnual(float taxaAnual) {
		this.taxaAnual = taxaAnual;
	}

	public int getIngressantesAnual() {
		return ingressantesAnual;
	}

	public void setIngressantesAnual(int ingressantesAnual) {
		this.ingressantesAnual = ingressantesAnual;
	}

	public int getConcluintesAnual() {
		return concluintesAnual;
	}

	public void setConcluintesAnual(int concluintesAnual) {
		this.concluintesAnual = concluintesAnual;
	}		
}