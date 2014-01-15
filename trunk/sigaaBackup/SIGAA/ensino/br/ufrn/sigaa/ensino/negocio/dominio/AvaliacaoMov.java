/*
 * Sistema Integrado de Patrim�nio e Administra��o de Contratos
 * Superintend�ncia de Inform�tica - UFRN
 * 
 * Created on 22/09/2006
 *
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Movimenta��o para cadastro de avalia��es
 *
 * @author David Ricardo
 *
 */
public class AvaliacaoMov extends AbstractMovimentoAdapter {

	private Turma turma;
	
	private int unidade;
	
	private Avaliacao avaliacao;
	
	/**
	 * @return the avaliacao
	 */
	public Avaliacao getAvaliacao() {
		return avaliacao;
	}

	/**
	 * @param avaliacao the avaliacao to set
	 */
	public void setAvaliacao(Avaliacao avaliacao) {
		this.avaliacao = avaliacao;
	}

	/**
	 * @return the idTurma
	 */
	public Turma getTurma() {
		return turma;
	}

	/**
	 * @param idTurma the idTurma to set
	 */
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/**
	 * @return the unidade
	 */
	public int getUnidade() {
		return unidade;
	}

	/**
	 * @param unidade the unidade to set
	 */
	public void setUnidade(int unidade) {
		this.unidade = unidade;
	}

}
