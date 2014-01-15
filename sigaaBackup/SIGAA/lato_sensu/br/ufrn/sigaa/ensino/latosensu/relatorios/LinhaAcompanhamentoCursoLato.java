/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '13/07/2007'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.relatorios;

import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe auxiliar para gerar o quadro de acompanhamento do curso lato.
 * @author leonardo
 *
 */
public class LinhaAcompanhamentoCursoLato {

	private int idDisciplina;
	
	private String descricaoDisciplina;
	
	private String situacao;
	
	private ComponenteCurricular disciplina;
	
	private Turma turma;

	/** construtor padrão sem argumentos */
	public LinhaAcompanhamentoCursoLato(){
	}

	public String getDescricaoDisciplina() {
		return descricaoDisciplina;
	}

	public void setDescricaoDisciplina(String descricaoDisciplina) {
		this.descricaoDisciplina = descricaoDisciplina;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public int getIdDisciplina() {
		return idDisciplina;
	}

	public void setIdDisciplina(int idDisciplina) {
		this.idDisciplina = idDisciplina;
	}
	
	public ComponenteCurricular getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}
}
