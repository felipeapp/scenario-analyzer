/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 10/11/2010
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.util.Collection;

import br.ufrn.sigaa.arq.dominio.MovimentoAcademicoAdapter;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * @author Leonardo Campos
 *
 */
public class MovimentoMatriculaHorario extends MovimentoAcademicoAdapter {

	private Collection<Turma> turmas;
	
	public MovimentoMatriculaHorario() {
	}

	public Collection<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}
	
	
}
