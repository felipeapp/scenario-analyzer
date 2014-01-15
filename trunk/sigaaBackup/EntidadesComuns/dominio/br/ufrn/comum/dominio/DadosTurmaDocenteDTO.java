package br.ufrn.comum.dominio;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Usado para encapsular dados de uma turma
 * 
 * @author Henrique André
 * 
 */
public class DadosTurmaDocenteDTO {

	private int idTurma;
	private String codigoTurma;
	private String disciplina;
	private Set<Date> aulas;
	private String descricaohorario;
	
	/**
	 * Mapa para guardar os horários da turma, no formato Map<horaInicio, horaFim>
	 */
	private Map<Date, Date> horariosTurma;

	public int getIdTurma() {
		return idTurma;
	}

	public void setIdTurma(int idTurma) {
		this.idTurma = idTurma;
	}

	public String getCodigoTurma() {
		return codigoTurma;
	}

	public void setCodigoTurma(String codigoTurma) {
		this.codigoTurma = codigoTurma;
	}

	public String getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(String disciplina) {
		this.disciplina = disciplina;
	}

	public Set<Date> getAulas() {
		return aulas;
	}

	public void setAulas(Set<Date> aulas) {
		this.aulas = aulas;
	}

	public Map<Date, Date> getHorariosTurma() {
		return horariosTurma;
	}

	public void setHorariosTurma(Map<Date, Date> horariosTurma) {
		this.horariosTurma = horariosTurma;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idTurma;
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
		DadosTurmaDocenteDTO other = (DadosTurmaDocenteDTO) obj;
		if (idTurma != other.idTurma)
			return false;
		return true;
	}

	/**
	 * Retorna uma representação HTML dos dados da turma.
	 */
	public String toString(){
		StringBuilder str = new StringBuilder();
		str	.append("Turma " + codigoTurma)
			.append(" - ")
			.append(disciplina)
			.append("<div style=\"padding-left:10px;\">")
			.append("Horário: " + descricaohorario);
		str.append("</div>");
		return str.toString();
	}

	public String getDescricaohorario() {
		return descricaohorario;
	}

	public void setDescricaohorario(String descricaohorario) {
		this.descricaohorario = descricaohorario;
	}
	
}
