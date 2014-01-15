package br.ufrn.sigaa.ead.dominio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Representa uma lista de alunos que pode se matricular em um conjunto de disciplinas
 * 
 * @author Henrique André
 *
 */
public class LoteMatriculas {

	private List<DiscenteGraduacao> discentes;
	private List<Turma> turmas;
	private Curso curso;

	public List<DiscenteGraduacao> getDiscentes() {
		return discentes;
	}

	public void setDiscentes(List<DiscenteGraduacao> discentes) {
		this.discentes = discentes;
	}

	public List<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(List<Turma> turmas) {
		this.turmas = turmas;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public void addTurma(Turma t) {
		if (turmas == null)
			turmas = new ArrayList<Turma>();
		
		turmas.add(t);
		
	}
	
}
