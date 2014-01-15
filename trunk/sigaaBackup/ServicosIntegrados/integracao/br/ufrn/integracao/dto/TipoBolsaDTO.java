/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 08/02/2010
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * DTO criado com a finalidade de auxiliar a projeção do método BolsaImpl.findByMatriculaSituacao()
 * que utiliza AliasToBeanResultTransformer.
 *  
 * @author agostinho campos
 *
 */
public class TipoBolsaDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private int idTipoBolsa;
	private String denominacao;
	
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the idTipoBolsa
	 */
	public int getIdTipoBolsa() {
		return idTipoBolsa;
	}
	/**
	 * @param idTipoBolsa the idTipoBolsa to set
	 */
	public void setIdTipoBolsa(int idTipoBolsa) {
		this.idTipoBolsa = idTipoBolsa;
	}
	
	public String getDenominacao() {
		return denominacao;
	}
	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

}
