/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 08/08/2011
 * 
 */
package br.ufrn.integracao.dto.biblioteca;

import java.io.Serializable;

/**
 *
 * <p>DTO utilizados para fazer a tranferência de patrimônio de materais informacionais entre 
 *   unidades organizacionais diferentes. </p>
 * 
 * @author jadson
 * @version 1.0 08/08/2011 criação da classe
 */
public class DadosTransferenciaMaterialDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**
	 * O número do patrimônio que está sendo transferido
	 */
	private Long numeroPatrimonio;
	
	/** Código da unidade origem do material */
	protected Long codigoUnidadeOrigem;
	
	/** A descrição da unidade origem do material */
	private String nomeUnidadeOrigem;
	
	/** Código da unidade destino do material */
	protected Long codigoUnidadeDestino;
	
	/** A descrição da unidade destino do material */
	private String nomeUnidadeDestino;

	/**
	 * Default construtor, obrigatório na utilização de WebServices
	 */
	public DadosTransferenciaMaterialDTO(){
		
	}
	
	/**
	 * Construtor que é para ser utilizado.
	 * 
	 * @param numeroPatrimonio
	 * @param codigoUnidadeOrigem
	 * @param nomeUnidadeOrigem
	 * @param codigoUnidadeDestino
	 * @param nomeUnidadeDestino
	 */
	public DadosTransferenciaMaterialDTO(Long numeroPatrimonio, Long codigoUnidadeOrigem, String nomeUnidadeOrigem
			, Long codigoUnidadeDestino, String nomeUnidadeDestino) {
		this.numeroPatrimonio = numeroPatrimonio;
		this.codigoUnidadeOrigem = codigoUnidadeOrigem;
		this.nomeUnidadeOrigem = nomeUnidadeOrigem;
		this.codigoUnidadeDestino = codigoUnidadeDestino;
		this.nomeUnidadeDestino = nomeUnidadeDestino;
	}

	
	
	// todos os sets e gets obrigatórios para poder utilizar web-services
	
	@Override
	public String toString() {
		return "O bem: "+getNumeroPatrimonio()
			+" foi tranferido  : "+getCodigoUnidadeOrigem()+" - "+getNomeUnidadeOrigem()+ " para a unidade: "
			+getCodigoUnidadeDestino()+" - "+getNomeUnidadeDestino();
	}

	public Long getNumeroPatrimonio() {
		return numeroPatrimonio;
	}

	public Long getCodigoUnidadeOrigem() {
		return codigoUnidadeOrigem;
	}

	public String getNomeUnidadeOrigem() {
		return nomeUnidadeOrigem;
	}

	public Long getCodigoUnidadeDestino() {
		return codigoUnidadeDestino;
	}

	public String getNomeUnidadeDestino() {
		return nomeUnidadeDestino;
	}

	public void setNumeroPatrimonio(Long numeroPatrimonio) {
		this.numeroPatrimonio = numeroPatrimonio;
	}

	public void setCodigoUnidadeOrigem(Long codigoUnidadeOrigem) {
		this.codigoUnidadeOrigem = codigoUnidadeOrigem;
	}

	public void setNomeUnidadeOrigem(String nomeUnidadeOrigem) {
		this.nomeUnidadeOrigem = nomeUnidadeOrigem;
	}

	public void setCodigoUnidadeDestino(Long codigoUnidadeDestino) {
		this.codigoUnidadeDestino = codigoUnidadeDestino;
	}

	public void setNomeUnidadeDestino(String nomeUnidadeDestino) {
		this.nomeUnidadeDestino = nomeUnidadeDestino;
	}
	
}
