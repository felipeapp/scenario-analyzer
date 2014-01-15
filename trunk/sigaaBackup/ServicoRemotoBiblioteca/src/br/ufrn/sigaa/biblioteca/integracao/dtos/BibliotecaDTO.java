/*
 * BibliotecaDTO.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.integracao.dtos;

import java.io.Serializable;

/**
 * <p>Classe criada para passar os dados da biblioteca para os sistemas remotos que se comunição 
 * com o módulo da biblioteca.</p>
 *
 * @author jadson
 * @since 19/12/2008
 * @version 1.0 criação da classe
 * @version 1.1 06/09/2010 mudança para o pacote de integração da arquitetura.
 */
public class BibliotecaDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/** id da biblioteca real */
	public int idBiblioteca; 
	
	/** a descricao da biblioteca real*/
	public String descricaoBiblioteca;  // 

	
	/**
     * Construtor default, obrigatórios nos DTOs.
     */
	public BibliotecaDTO() {
		super();
	}

	/**
	 * Construtor default and unique
	 * 
	 * @param idBiblioteca
	 * @param descricaoBiblioteca
	 */
	public BibliotecaDTO(int idBiblioteca, String descricaoBiblioteca) {
		this.idBiblioteca = idBiblioteca;
		this.descricaoBiblioteca = descricaoBiblioteca;
	}

	public String getDescricaoBiblioteca() {
		return descricaoBiblioteca;
	}

	public int getIdBiblioteca() {
		return idBiblioteca;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idBiblioteca;
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
		BibliotecaDTO other = (BibliotecaDTO) obj;
		if (idBiblioteca != other.idBiblioteca)
			return false;
		return true;
	}

	/**
	 * Usado na hora de mostrar a biblioteca para o usuario
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return descricaoBiblioteca;
	}
	
	
	
}
