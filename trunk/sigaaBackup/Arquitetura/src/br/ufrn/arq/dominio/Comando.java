/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/09/2006
 */
package br.ufrn.arq.dominio;

import java.io.Serializable;

/**
 * Entidade que representa um comando do sistema
 * 
 * @author Gleydson Lima
 *
 */
public class Comando implements Serializable {

	private int id;

	private String classe;

	public Comando(int id, String classe) {
		this.id = id;
		this.classe = classe;
	}

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		Comando other = (Comando) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getId() + " " + getClasse();
	}

}
