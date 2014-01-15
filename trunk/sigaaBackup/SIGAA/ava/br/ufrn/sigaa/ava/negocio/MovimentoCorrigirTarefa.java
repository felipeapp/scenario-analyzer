package br.ufrn.sigaa.ava.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ava.dominio.RespostaTarefaTurma;

public class MovimentoCorrigirTarefa extends AbstractMovimentoAdapter {

	private RespostaTarefaTurma resposta;

	public MovimentoCorrigirTarefa (RespostaTarefaTurma resposta) {
		this.resposta = resposta;
	}
	
	/**
	 * @return the tarefas
	 */
	public RespostaTarefaTurma getResposta() {
		return resposta;
	}

	/**
	 * @param tarefas the tarefas to set
	 */
	public void setRespostas(RespostaTarefaTurma resposta) {
		this.resposta = resposta;
	}

}

