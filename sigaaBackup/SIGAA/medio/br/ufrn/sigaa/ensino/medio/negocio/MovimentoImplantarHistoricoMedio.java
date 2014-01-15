/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 30/06/2011
* 
*/
package br.ufrn.sigaa.ensino.medio.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Movimento que auxilia na persistência da implantação do histórico do discente médio
 * 
 * @author Arlindo
 *
 */
public class MovimentoImplantarHistoricoMedio extends AbstractMovimentoAdapter {
	
	/** Discente movimentado */
	private Discente discente;
	
	/** Listas das disciplinas que serão implantadas no histórico */
	private List<MatriculaComponente> matriculas;

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public List<MatriculaComponente> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(List<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

}
