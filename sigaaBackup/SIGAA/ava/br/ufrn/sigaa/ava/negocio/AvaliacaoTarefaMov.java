/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.ava.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ava.dominio.RespostaTarefaTurma;

/**
 * Movimento para cadastro da avalia��o de uma tarefa pelo professor
 * @author David Pereira
 *
 */
public class AvaliacaoTarefaMov extends AbstractMovimentoAdapter {

	private List<RespostaTarefaTurma> respostas;

	/**
	 * @return the tarefas
	 */
	public List<RespostaTarefaTurma> getRespostas() {
		return respostas;
	}

	/**
	 * @param tarefas the tarefas to set
	 */
	public void setRespostas(List<RespostaTarefaTurma> respostas) {
		this.respostas = respostas;
	}

}
