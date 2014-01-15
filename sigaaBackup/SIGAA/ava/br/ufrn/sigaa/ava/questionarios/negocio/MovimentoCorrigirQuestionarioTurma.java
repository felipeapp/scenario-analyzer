package br.ufrn.sigaa.ava.questionarios.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.questionarios.dominio.EnvioRespostasQuestionarioTurma;

public class MovimentoCorrigirQuestionarioTurma extends AbstractMovimentoAdapter {

	private EnvioRespostasQuestionarioTurma resposta;

	public MovimentoCorrigirQuestionarioTurma (EnvioRespostasQuestionarioTurma resposta) {
		this.resposta = resposta;
		
		this.setCodMovimento(SigaaListaComando.CORRIGIR_QUESTIONARIO_TURMA);
	}

	public EnvioRespostasQuestionarioTurma getResposta() {
		return resposta;
	}

	public void setResposta(EnvioRespostasQuestionarioTurma resposta) {
		this.resposta = resposta;
	}
	
	
}

