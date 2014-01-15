/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Movimento para operações com discentes de graduação
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
