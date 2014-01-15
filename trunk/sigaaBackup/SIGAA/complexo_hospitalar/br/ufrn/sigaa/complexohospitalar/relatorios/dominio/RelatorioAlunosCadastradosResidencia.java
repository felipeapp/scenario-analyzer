/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 18/04/2011
 *
 */
package br.ufrn.sigaa.complexohospitalar.relatorios.dominio;

import br.ufrn.sigaa.dominio.Unidade;

/**
 * Classe de domínio para geração do relatório de Alunos cadastrados nos programas de residência médica
 * 
 * @author arlindo
 *
 */
public class RelatorioAlunosCadastradosResidencia {
	
	/** Programa da residência */
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
