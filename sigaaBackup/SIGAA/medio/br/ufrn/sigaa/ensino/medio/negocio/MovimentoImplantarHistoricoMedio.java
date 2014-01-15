/*
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
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
 * Movimento que auxilia na persist�ncia da implanta��o do hist�rico do discente m�dio
 * 
 * @author Arlindo
 *
 */
public class MovimentoImplantarHistoricoMedio extends AbstractMovimentoAdapter {
	
	/** Discente movimentado */
	private Discente discente;
	
	/** Listas das disciplinas que ser�o implantadas no hist�rico */
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
