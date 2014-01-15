/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 *  Created on 22/01/2010
 *
 */
package br.ufrn.sigaa.ead.dominio;

import java.util.Collection;
import java.util.List;

import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Representa um lote de turmas que o discente vai se matricular
 * 
 * @author Henrique André
 */
public class LoteMatriculasDiscente {
	
	private DiscenteAdapter discente;
	private List<Turma> turmas;
	private Curso curso;
	private CalendarioAcademico calendario;
	
	//
	//Collection<ComponenteCurricular> componentesPagoseMatriculado;
	Collection<ComponenteCurricular> todosComponentes;
	Collection<Turma> turmasSemestre;
	//Collection<Turma> turmasSolicitadas;
	Collection<ComponenteCurricular> componentesConcluidosDiscente;

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
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

	public CalendarioAcademico getCalendario() {
		return calendario;
	}

	public void setCalendario(CalendarioAcademico calendario) {
		this.calendario = calendario;
	}

	public Collection<ComponenteCurricular> getTodosComponentes() {
		return todosComponentes;
	}

	public void setTodosComponentes(
			Collection<ComponenteCurricular> todosComponentes) {
		this.todosComponentes = todosComponentes;
	}

	public Collection<Turma> getTurmasSemestre() {
		return turmasSemestre;
	}

	public void setTurmasSemestre(Collection<Turma> turmasSemestre) {
		this.turmasSemestre = turmasSemestre;
	}

	public Collection<ComponenteCurricular> getComponentesConcluidosDiscente() {
		return componentesConcluidosDiscente;
	}

	public void setComponentesConcluidosDiscente(
			Collection<ComponenteCurricular> componentesConcluidosDiscente) {
		this.componentesConcluidosDiscente = componentesConcluidosDiscente;
	}


}
