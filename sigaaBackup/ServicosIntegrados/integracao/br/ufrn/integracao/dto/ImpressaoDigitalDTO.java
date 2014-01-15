/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 26/01/2012
 */
package br.ufrn.integracao.dto;

/**
 * DTO para transferência das informações das
 * impressões digitais das pessoas que irão utilizar o
 * sistema de ponto biométrico.
 * 
 * @author Rafael Moreira
 * @author David Pereira
 */
public class ImpressaoDigitalDTO {
	
	/** CPF da pessoa que possui a impressão digital */
	private long cpf;
	
	/** Array de bytes contendo informações biométricas sobre a impressão digital da pessoa */
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
