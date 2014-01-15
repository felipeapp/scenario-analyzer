/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 18/04/2011
 *
 */
package br.ufrn.sigaa.complexohospitalar.relatorios.dominio;

import br.ufrn.sigaa.dominio.Unidade;

/**
 * Classe de dom�nio para gera��o do relat�rio de Alunos cadastrados nos programas de resid�ncia m�dica
 * 
 * @author arlindo
 *
 */
public class RelatorioAlunosCadastradosResidencia {
	
	/** Programa da resid�ncia */
	private Unidade unidade;
	
	/** total de alunos ativos no programa */
	private int total;

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
