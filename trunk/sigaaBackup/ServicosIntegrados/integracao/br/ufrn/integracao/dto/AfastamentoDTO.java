/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 26/01/2012
 */
package br.ufrn.integracao.dto;

import java.util.Date;

/**
 * DTO para um afastamento do servidor cadastrado no SIGRH.
 *
 * @author Bráulio
 */
public class AfastamentoDTO {

	/** Motivo do afastamento. */
	private String descricaoMotivo;
	
	/** Alguma observação sobre o afastamento. */
	private String observacao;
	
	/** Matrícula do servidor. */
	private int matriculaSiape;
	
	/** CPF do servidor. */
	private long cpf;
	
	/** Data de início do afastamento. */
	private Date inicio;
	
	/** Data de término do afastamento. */
	private Date fim;
	
	/** Número do documento/diploma legal. */
	private String numeroDocumento;
	
	/** Contém os ids das localidades do afastamento. */
	private int[] localidades;

	public String getDescricaoMotivo() {
		return descricaoMotivo;
	}

	public void setDescricaoMotivo( String descricaoMotivo ) {
		this.descricaoMotivo = descricaoMotivo;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao( String observacao ) {
		this.observacao = observacao;
	}

	public int getMatriculaSiape() {
		return matriculaSiape;
	}

	public void setMatriculaSiape( int matriculaSiape ) {
		this.matriculaSiape = matriculaSiape;
	}

	public long getCpf() {
		return cpf;
	}

	public void setCpf( long cpf ) {
		this.cpf = cpf;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio( Date inicio ) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim( Date fim ) {
		this.fim = fim;
	}

	public String getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento( String numeroDocumento ) {
		this.numeroDocumento = numeroDocumento;
	}

	public int[] getLocalidades() {
		return localidades;
	}

	public void setLocalidades( int[] localidades ) {
		this.localidades = localidades;
	}
	
}
