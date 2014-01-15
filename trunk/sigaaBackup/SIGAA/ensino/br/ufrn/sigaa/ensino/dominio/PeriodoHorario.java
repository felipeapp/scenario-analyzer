/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '15/12/2009'
 *
 */

package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

/**
 * Classe para facilitar a manipulação dos horários, quando a turma possuir um {@link ComponenteCurricular} que permita horários flexíveis.
 * <br>
 * Período possui uma data de inicio e outro de fim.
 * 
 * @author Henrique André
 */
public class PeriodoHorario {
	/**
	 * Inicio do período
	 */
	private Date inicio;

	/**
	 * Fim do período
	 */
	private Date fim;

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fim == null) ? 0 : fim.hashCode());
		result = prime * result + ((inicio == null) ? 0 : inicio.hashCode());
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
		PeriodoHorario other = (PeriodoHorario) obj;
		if (fim == null) {
			if (other.fim != null)
				return false;
		} else if (!fim.equals(other.fim))
			return false;
		if (inicio == null) {
			if (other.inicio != null)
				return false;
		} else if (!inicio.equals(other.inicio))
			return false;
		return true;
	}

}
