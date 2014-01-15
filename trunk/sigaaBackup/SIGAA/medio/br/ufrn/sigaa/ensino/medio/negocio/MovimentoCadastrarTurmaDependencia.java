/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 16/01/2013
 *
 */
package br.ufrn.sigaa.ensino.medio.negocio;

import java.util.List;

import br.ufrn.arq.dominio.MovimentoCadastro;

/**
 *  Movimento para alterar as turmas de dependencia de ensino médio.
 * @author Joab Galdino
 *
 */
public class MovimentoCadastrarTurmaDependencia extends MovimentoCadastro{
	/** Lista guarda os ids das disciplinas selecionadas para cadastro*/
	private List<Integer> disciplinasSelecionadas;
	
	/** Lista guarda os ids das disciplinas que ainda não forma persistidas*/
	private List<Integer> disciplinasAdicionadas;

	/**
	 * @return Retorna a(s) disciplinasSelecionadas.
	 */
	public List<Integer> getdisciplinasSelecionadas() {
		return disciplinasSelecionadas;
	}

	/**
	 * @param disciplinasSelecionadas Altera a(s) disciplinasSelecionadas.
	 */
	public void setdisciplinasSelecionadas(List<Integer> disciplinasSelecionadas) {
		this.disciplinasSelecionadas = disciplinasSelecionadas;
	}

	/**
	 * @return Retorna a(s) disciplinasAdicionadas.
	 */
	public List<Integer> getdisciplinasAdicionadas() {
		return disciplinasAdicionadas;
	}

	/**
	 * @param disciplinasAdicionadas Altera a(s) disciplinasAdicionadas.
	 */
	public void setdisciplinasAdicionadas(List<Integer> disciplinasAdicionadas) {
		this.disciplinasAdicionadas = disciplinasAdicionadas;
	}
}
