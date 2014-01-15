/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 26/01/2012
 */
package br.ufrn.integracao.dto;

/**
 * DTO para transfer�ncia das informa��es das
 * impress�es digitais das pessoas que ir�o utilizar o
 * sistema de ponto biom�trico.
 * 
 * @author Rafael Moreira
 * @author David Pereira
 */
public class ImpressaoDigitalDTO {
	
	/** CPF da pessoa que possui a impress�o digital */
	private long cpf;
	
	/** Array de bytes contendo informa��es biom�tricas sobre a impress�o digital da pessoa */
	private byte[] digital;
	
	/** Dedo utilizado para coletar a digital da pessoa */
	private char dedoColetado;

	public long getCpf() {
		return cpf;
	}

	public void setCpf(long cpf) {
		this.cpf = cpf;
	}

	public byte[] getDigital() {
		return digital;
	}

	public void setDigital(byte[] digital) {
		this.digital = digital;
	}

	public char getDedoColetado() {
		return dedoColetado;
	}

	public void setDedoColetado(char dedoColetado) {
		this.dedoColetado = dedoColetado;
	}
}
