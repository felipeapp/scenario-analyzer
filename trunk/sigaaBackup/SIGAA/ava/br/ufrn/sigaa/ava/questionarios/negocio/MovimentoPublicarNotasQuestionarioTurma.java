package br.ufrn.sigaa.ava.questionarios.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;

public class MovimentoPublicarNotasQuestionarioTurma extends AbstractMovimentoAdapter {

	private QuestionarioTurma questionario;

	public MovimentoPublicarNotasQuestionarioTurma(QuestionarioTurma questionario) {
		super();
		this.questionario = questionario;
		
		setCodMovimento(SigaaListaComando.PUBLICAR_NOTAS_QUESTIONARIO_TURMA);
	}

	public QuestionarioTurma getQuestionario() {
		return questionario;
	}

	public void setQuestionario(QuestionarioTurma questionario) {
		this.questionario = questionario;
	}
}