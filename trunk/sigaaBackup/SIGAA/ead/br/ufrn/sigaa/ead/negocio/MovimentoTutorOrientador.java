/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/19 - 19:30:51
 */
package br.ufrn.sigaa.ead.negocio;

import java.util.ArrayList;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Managed para operações com o tutor.
 *
 * @author Diego Jácome
 *
 */
public class MovimentoTutorOrientador extends MovimentoCadastro {
	
	/** Lista de turmas removidas (no qual o tutor perderá permissão). */
	private ArrayList<Turma> turmasARemover;

	public void setTurmasARemover(ArrayList<Turma> turmasARemover) {
		this.turmasARemover = turmasARemover;
	}

	public ArrayList<Turma> getTurmasARemover() {
		return turmasARemover;
	}
	
}