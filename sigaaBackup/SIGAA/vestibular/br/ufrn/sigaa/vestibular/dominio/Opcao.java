/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 07/12/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.vestibular.dominio;

/**
 * @author Rafael Gomes
 *
 */
public class Opcao {
	
	private Integer ordemOpcao;
	private Integer classificacao; 
	private double argumentoFinal;
	private double argumentoFinalSemBeneficio;
	private Integer matrizCurricular;
	
	/**
	 * @return the ordemOpcao
	 */
	public Integer getOrdemOpcao() {
		return ordemOpcao;
	}
	/**
	 * @param ordemOpcao the ordemOpcao to set
	 */
	public void setOrdemOpcao(Integer ordemOpcao) {
		this.ordemOpcao = ordemOpcao;
	}
	/**
	 * @return the classificacao
	 */
	public Integer getClassificacao() {
		return classificacao;
	}
	/**
	 * @param classificacao the classificacao to set
	 */
	public void setClassificacao(Integer classificacao) {
		this.classificacao = classificacao;
	}
	/**
	 * @return the argumentoFinal
	 */
	public double getArgumentoFinal() {
		return argumentoFinal;
	}
	/**
	 * @param argumentoFinal the argumentoFinal to set
	 */
	public void setArgumentoFinal(double argumentoFinal) {
		this.argumentoFinal = argumentoFinal;
	}
	/**
	 * @return the argumentoFinalSemBeneficio
	 */
	public double getArgumentoFinalSemBeneficio() {
		return argumentoFinalSemBeneficio;
	}
	/**
	 * @param argumentoFinalSemBeneficio the argumentoFinalSemBeneficio to set
	 */
	public void setArgumentoFinalSemBeneficio(double argumentoFinalSemBeneficio) {
		this.argumentoFinalSemBeneficio = argumentoFinalSemBeneficio;
	}
	/**
	 * @return the matrizCurricular
	 */
	public Integer getMatrizCurricular() {
		return matrizCurricular;
	}
	/**
	 * @param matrizCurricular the matrizCurricular to set
	 */
	public void setMatrizCurricular(Integer matrizCurricular) {
		this.matrizCurricular = matrizCurricular;
	}
	
	
	
	
}
