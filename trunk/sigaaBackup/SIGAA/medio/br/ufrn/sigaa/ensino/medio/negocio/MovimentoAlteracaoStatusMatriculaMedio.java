/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 05/07/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;

/**
 * Movimento para realizar altera��o de status de matriculas do n�vel m�dio
 *
 * @author Arlindo
 *
 */
public class MovimentoAlteracaoStatusMatriculaMedio extends AbstractMovimentoAdapter {
	
	/**
	 * Atributo usado no caso da opera��o ser realizada sobre um conjunto de matr�culas
	 */
	private Collection<MatriculaDiscenteSerie> matriculas;
	
	/** Turma da s�rie que ser� alterada a matricula */
	private TurmaSerie turmaSerie;

	public Collection<MatriculaDiscenteSerie> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(Collection<MatriculaDiscenteSerie> matriculas) {
		this.matriculas = matriculas;
	}

	public TurmaSerie getTurmaSerie() {
		return turmaSerie;
	}

	public void setTurmaSerie(TurmaSerie turmaSerie) {
		this.turmaSerie = turmaSerie;
	}

}
