/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 26/04/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.graduacao.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe auxiliar utilizada para o ger�nciamento de transfer�ncia de alunos entre turmas. (N�o persistida)
 * 
 * @author Rafael Gomes
 *
 */
public class TurmaOrigemTurmaDestinos {

	/** Objeto do tipo Turma utilizada para armazenar a turma de origem da transfer�ncia.*/
	Turma turma = new Turma();
	/** Objeto do tipo Turma utilizada para armazenar a turma de destino da transfer�ncia.*/
	Turma turmaDestino = new Turma();
	/** Matr�cula em componente do discente em transfer�ncia.*/
	MatriculaComponente matricula = new MatriculaComponente();
	/** Lista contendo as turmas de destino da transfer�ncia.*/
	List<Turma> listTurmasDestino = new ArrayList<Turma>();
	/** Cole��o do tipo selectItem para as turmas de destino.*/
	Collection<SelectItem> selectTurmasDestino = new ArrayList<SelectItem>();
	
	public Turma getTurma() {
		return turma;
	}
	public void setTurma(Turma turma) {
		this.turma = turma;
	}
	public Turma getTurmaDestino() {
		return turmaDestino;
	}
	public void setTurmaDestino(Turma turmaDestino) {
		this.turmaDestino = turmaDestino;
	}
	public MatriculaComponente getMatricula() {
		return matricula;
	}
	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
	}
	public List<Turma> getListTurmasDestino() {
		return listTurmasDestino;
	}
	public void setListTurmasDestino(List<Turma> listTurmasDestino) {
		this.listTurmasDestino = listTurmasDestino;
	}
	public Collection<SelectItem> getSelectTurmasDestino() {
		return selectTurmasDestino;
	}
	public void setSelectTurmasDestino(
			Collection<SelectItem> selectTurmasDestino) {
		this.selectTurmasDestino = selectTurmasDestino;
	}
	
}
