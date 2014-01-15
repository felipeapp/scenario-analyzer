/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 16/03/2011
 *
 */
package br.ufrn.sigaa.relatoriosgestao.dominio;

import br.ufrn.sigaa.dominio.Unidade;



/**
 * Sumário de PID (Plano Individual do Docente) por Semestre
 *
 * @author Arlindo
 *
 */
public class RelatorioPIDPorSemestre {
	
	/**
	 * Departamento responsável
	 */
	private Unidade unidade;
	
	/** Total de docentes ativos */
	private int totalDocentesAtivos;
	
	/** Total de PIDs Cadastrados */
	private int totalCadastrados;
	
	/** Total de PIDs Homologados */
	private int totalHomologados;

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public int getTotalDocentesAtivos() {
		return totalDocentesAtivos;
	}

	public void setTotalDocentesAtivos(int totalDocentesAtivos) {
		this.totalDocentesAtivos = totalDocentesAtivos;
	}

	public int getTotalCadastrados() {
		return totalCadastrados;
	}

	public void setTotalCadastrados(int totalCadastrados) {
		this.totalCadastrados = totalCadastrados;
	}

	public int getTotalHomologados() {
		return totalHomologados;
	}

	public void setTotalHomologados(int totalHomologados) {
		this.totalHomologados = totalHomologados;
	}
}
