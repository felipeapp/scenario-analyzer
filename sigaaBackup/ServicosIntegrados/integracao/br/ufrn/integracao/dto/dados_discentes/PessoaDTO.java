/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 25/06/2013
 * Autor: Diego Jácome
 */
package br.ufrn.integracao.dto.dados_discentes;

import java.io.Serializable;
import java.util.List;

/**
 * DTO para recepção dos dados de discente via o serviço do SIGAA.
 * 
 * @author Diego Jácome
 *
 */
public class PessoaDTO implements Serializable{

	/** Id da pessoa. */
	private Integer idPessoa;
	
	/** Discentes vinculados a pessoa. */
	private List<DiscenteDTO> discentes;
	
	public PessoaDTO() {}
	
	public PessoaDTO(Integer idPessoa) {
		this.idPessoa = idPessoa;
	}

	public void setDiscentes(List<DiscenteDTO> discentes) {
		this.discentes = discentes;
	}

	public List<DiscenteDTO> getDiscentes() {
		return discentes;
	}

	public void setIdPessoa(Integer idPessoa) {
		this.idPessoa = idPessoa;
	}

	public Integer getIdPessoa() {
		return idPessoa;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((idPessoa == null) ? 0 : idPessoa.hashCode());
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
		PessoaDTO other = (PessoaDTO) obj;
		if (idPessoa == null) {
			if (other.idPessoa != null)
				return false;
		} else if (!idPessoa.equals(other.idPessoa))
			return false;
		return true;
	}
		
}
