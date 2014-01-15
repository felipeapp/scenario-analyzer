/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 09/05/2012
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * Data Transfer Object para informa��es do participe.
 * 
 * @author Eduardo Costa (UFRN)
 *
 */
public class ParticipeDTO implements Serializable {
	
	/** Representa o nome do part�cipe.*/
	private String nome;

	/** Representa o cpf ou cnpj do part�cipe.*/
	private Long cpfCNPJ;

	/** Representa o identificador do tipo convenete.*/
	private Integer idTipoConvenente;

	/** Representa o nome do tipo convenete.*/
	private String tipoConvenente;

	/** Indica se o part�cipe � financiador.*/
	private Boolean financiador;
	
	private PessoaProjetoDTO pessoaDTO;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getCpfCNPJ() {
		return cpfCNPJ;
	}

	public void setCpfCNPJ(Long cpfCNPJ) {
		this.cpfCNPJ = cpfCNPJ;
	}

	public Integer getIdTipoConvenente() {
		return idTipoConvenente;
	}

	public void setIdTipoConvenente(Integer idTipoConvenente) {
		this.idTipoConvenente = idTipoConvenente;
	}

	public String getTipoConvenente() {
		return tipoConvenente;
	}

	public void setTipoConvenente(String tipoConvenente) {
		this.tipoConvenente = tipoConvenente;
	}

	public Boolean getFinanciador() {
		return financiador;
	}

	public void setFinanciador(Boolean financiador) {
		this.financiador = financiador;
	}

	public void setPessoaDTO(PessoaProjetoDTO pessoaDTO) {
		this.pessoaDTO = pessoaDTO;
	}

	public PessoaProjetoDTO getPessoaDTO() {
		return pessoaDTO;
	}
	
}
