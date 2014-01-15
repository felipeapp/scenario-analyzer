package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
 * Representa o material informacional onde contém as informações a serem trocadas entre os sistemas.
 * 
 * @author Mário Melo
 *
 */
public class MaterialInformacionalDTO implements Serializable{
	
	private int idMaterialInformacional;
	
	private int qtdAcervo;

	public int getIdMaterialInformacional() {
		return idMaterialInformacional;
	}

	public void setIdMaterialInformacional(int idMaterialInformacional) {
		this.idMaterialInformacional = idMaterialInformacional;
	}

	public int getQtdAcervo() {
		return qtdAcervo;
	}

	public void setQtdAcervo(int qtdAcervo) {
		this.qtdAcervo = qtdAcervo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idMaterialInformacional;
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
		MaterialInformacionalDTO other = (MaterialInformacionalDTO) obj;
		if (idMaterialInformacional != other.idMaterialInformacional)
			return false;
		return true;
	}
	
	
}
