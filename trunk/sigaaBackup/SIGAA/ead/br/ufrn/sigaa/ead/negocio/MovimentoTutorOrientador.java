/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/19 - 19:30:51
 */
package br.ufrn.sigaa.ead.negocio;

import java.util.ArrayList;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Managed para opera��es com o tutor.
 *
 * @author Diego J�come
 *
 */
public class MovimentoTutorOrientador extends MovimentoCadastro {
	
	/** Lista de turmas removidas (no qual o tutor perder� permiss�o). */
	private ArrayList<Turma> turmasARemover;

	public void setTurmasARemover(ArrayList<Turma> turmasARemover) {
		this.turmasARemover = turmasARemover;
	}

	public ArrayList<Turma> getTurmasARemover() {
		return turmasARemover;
	}
	
}