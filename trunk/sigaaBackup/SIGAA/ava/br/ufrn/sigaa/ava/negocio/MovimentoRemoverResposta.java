package br.ufrn.sigaa.ava.negocio;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.ava.dominio.RespostaTarefaTurma;

public class MovimentoRemoverResposta extends MovimentoCadastro {

	private RespostaTarefaTurma resposta;

	public MovimentoRemoverResposta (RespostaTarefaTurma resposta) {
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
