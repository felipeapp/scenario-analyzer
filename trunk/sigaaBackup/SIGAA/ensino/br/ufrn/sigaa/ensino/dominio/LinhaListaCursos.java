/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 19/04/2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Classe auxiliar para gerar uma lista de cursos na área pública do sigaa
 * 
 * @author Leonardo
 *
 */
public class LinhaListaCursos {

	private Unidade unidade;
	
	private long numeroCursos;
	
	private Collection<Curso> cursos;
	
	public LinhaListaCursos(){
		cursos = new ArrayList<Curso>();
	}

	public void addCurso(Curso curso) {
		cursos.add(curso);
	}
	
	public Collection<Curso> getCursos() {
		return cursos;
	}

	public void setCursos(Collection<Curso> cursos) {
		this.cursos = cursos;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public long getNumeroCursos() {
		return numeroCursos;
	}

	public void setNumeroCursos(long numeroCursos) {
		this.numeroCursos = numeroCursos;
	}

}
