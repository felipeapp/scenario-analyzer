/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Movimento para opera��es com discentes de gradua��o
 *
 * @author Ricardo Wendell
 *
 */
public class MovimentoDiscenteGraduacao extends AbstractMovimentoAdapter {

	private DiscenteGraduacao discente;

	public MovimentoDiscenteGraduacao() {

	}

	public DiscenteGraduacao getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}



}
