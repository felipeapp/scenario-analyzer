/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/04/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.relatorios;

import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;

/**
 * classe auxiliar para gerar relatórios de alunos por curso lato sensu
 * 
 * @author Leonardo
 *
 */
public class LinhaRelatorioSinteticoAlunosCurso {

	private CursoLato curso;
	
	private long numeroAlunos;
	
	private long numeroAlunosMatriculados;
	
	private long numeroAlunosConcluido;
	
	public LinhaRelatorioSinteticoAlunosCurso(){
		
	}

	public CursoLato getCurso() {
		return curso;
	}

	public void setCurso(CursoLato curso) {
		this.curso = curso;
	}

	public long getNumeroAlunos() {
		return numeroAlunos;
	}

	public void setNumeroAlunos(long numeroAlunos) {
		this.numeroAlunos = numeroAlunos;
	}

	public long getNumeroAlunosMatriculados() {
		return numeroAlunosMatriculados;
	}

	public void setNumeroAlunosMatriculados(long numeroAlunosMatriculados) {
		this.numeroAlunosMatriculados = numeroAlunosMatriculados;
	}

	public long getNumeroAlunosConcluido() {
		return numeroAlunosConcluido;
	}

	public void setNumeroAlunosConcluido(long numeroAlunosConcluido) {
		this.numeroAlunosConcluido = numeroAlunosConcluido;
	}
	
}
