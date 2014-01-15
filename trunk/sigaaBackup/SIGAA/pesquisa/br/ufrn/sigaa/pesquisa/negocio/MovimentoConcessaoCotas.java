/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/01/2007
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;

/**
 * Entidade responsável pelo movimento de concessão de cotas 
 * 
 * @author Jean Guerethes
 */
public class MovimentoConcessaoCotas extends AbstractMovimentoAdapter {

	private int[] planosTrabalho;

	private boolean[] concessoes;

	public MovimentoConcessaoCotas() {

	}

	public boolean[] getConcessoes() {
		return concessoes;
	}

	public int[] getPlanosTrabalho() {
		return planosTrabalho;
	}

	public void setConcessoes(boolean[] concessoes) {
		this.concessoes = concessoes;
	}

	public void setPlanosTrabalho(int[] planosTrabalho) {
		this.planosTrabalho = planosTrabalho;
	}

}
