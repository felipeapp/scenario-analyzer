/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '15/12/2009'
 *
 */


package br.ufrn.sigaa.ensino.dominio;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;

/**
 * Classe para facilitar a manipulação dos horários de uma turma quando está possuir um {@link ComponenteCurricular} que permita horário flexível.
 * <br>
 * Objetivo aqui é agrupar todos os horários que uma turma possui no mesmo {@link PeriodoHorario}.
 * 
 * @author Henrique André
 * 
 */
public class GrupoHorarios {
	/**
	 * Período dos Horários
	 */
	private PeriodoHorario periodo;

	/**
	 * Horarios que possuem inicio e fim batendo com os valores de periodo
	 */
	private Collection<HorarioTurma> horarios;

	public PeriodoHorario getPeriodo() {
		return periodo;
	}

	public void setPeriodo(PeriodoHorario periodo) {
		this.periodo = periodo;
	}

	public Collection<HorarioTurma> getHorarios() {
		return horarios;
	}

	public void setHorarios(Collection<HorarioTurma> horarios) {
		this.horarios = horarios;
	}

	/**
	 * Retorna o horario formatado
	 * JSP: /sigaa.war/graduacao/turma/horarios.jsp
	 * 
	 * @return
	 */
	public String getHorarioFormatado() {
		Turma t = new Turma();
		ComponenteCurricular cc = new ComponenteCurricular();
		t.setDisciplina(cc);
		t.setHorarios(new ArrayList<HorarioTurma>(horarios));
		String formatarCodigoHorarios = HorarioTurmaUtil.formatarCodigoHorarios(t);

		return formatarCodigoHorarios;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((horarios == null) ? 0 : horarios.hashCode());
		result = prime * result + ((periodo == null) ? 0 : periodo.hashCode());
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
		GrupoHorarios other = (GrupoHorarios) obj;
		if (horarios == null) {
			if (other.horarios != null)
				return false;
		} else if (!horarios.equals(other.horarios))
			return false;
		if (periodo == null) {
			if (other.periodo != null)
				return false;
		} else if (!periodo.equals(other.periodo))
			return false;
		return true;
	}

}
