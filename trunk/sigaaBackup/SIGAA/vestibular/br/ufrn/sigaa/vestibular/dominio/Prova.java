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
public class Prova {
	
	String descricaoProva;
	double argumento;
	Integer fase;
	
	/**
	 * @return the descricaoProva
	 */
	public String getDescricaoProva() {
		return descricaoProva;
	}
	/**
	 * @param descricaoProva the descricaoProva to set
	 */
	public void setDescricaoProva(String descricaoProva) {
		this.descricaoProva = descricaoProva;
	}
	/**
	 * @return the argumento
	 */
	public double getArgumento() {
		return argumento;
	}
	/**
	 * @param argumento the argumento to set
	 */
	public void setArgumento(double argumento) {
		this.argumento = argumento;
	}
	/**
	 * @return the fase
	 */
	public Integer getFase() {
		return fase;
	}
	/**
	 * @param fase the fase to set
	 */
	public void setFase(Integer fase) {
		this.fase = fase;
	}
	
	
}
