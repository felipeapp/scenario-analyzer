package br.ufrn.sigaa.biblioteca.integracao.dtos.inventario_acervo;

import java.io.Serializable;

/**
 * DTO com as informacoes do inventario necessarias para o coletor.
 * 
 * <strong>OBSERVACAO 1 .:</strong>   A VM do coletor trabalha na versao 1.3 do Java.
 * <strong>OBSERVACAO 2 .:</strong>   Nao usar acentos nos comentarios porque essas classes sao copiadas para o 
 * netbens e dar erro de codificacao se usar.
 * 
 * @author Felipe
 *
 */
public class InventarioDto implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	/** id do inventario real */
	public int idInventario;
	
	/** a descricao do inventario real*/
	public String descricaoInventario;
        
	public InventarioDto() {
            
    }

	public InventarioDto(int idInventario, String descricaoInventario) {
		this.idInventario = idInventario;
		this.descricaoInventario = descricaoInventario;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idInventario;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InventarioDto other = (InventarioDto) obj;
		if (idInventario != other.idInventario)
			return false;
		return true;
	}

	/**
	 * Usado na hora de mostrar o inventario para o usuario
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
            return descricaoInventario;
	}

}
