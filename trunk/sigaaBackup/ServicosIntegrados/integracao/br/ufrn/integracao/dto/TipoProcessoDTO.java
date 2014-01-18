/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 06/02/2013
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * Classe que representa um tipo de processo no protocolo.
 * 
 * @author Raphael Medeiros
 *
 */
public class TipoProcessoDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private int id;

    /**
     * Denominação do tipo de processo.
     */
    private String denominacao;

    /**
     * Indica se o tipo de processo encontra-se ativo ou não.
     */
    private boolean ativo;

    public TipoProcessoDTO() { }
    
	public TipoProcessoDTO(int id, String denominacao, boolean ativo) {
		this.id = id;
		this.denominacao = denominacao;
		this.ativo = ativo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TipoProcessoDTO other = (TipoProcessoDTO) obj;
		if (id != other.id)
			return false;
		return true;
	}
}