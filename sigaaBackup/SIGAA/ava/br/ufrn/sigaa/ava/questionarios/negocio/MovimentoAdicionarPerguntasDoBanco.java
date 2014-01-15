/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 30/10/2009
 */
package br.ufrn.sigaa.ava.questionarios.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.questionarios.dominio.PerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;

/**
 * Movimento que indica os ids das perguntas a serem adicionadas ao questionário.
 * 
 * @author Fred_Castro
 *
 */

public class MovimentoAdicionarPerguntasDoBanco extends AbstractMovimentoAdapter {
	
	/**
	 * O questionário que receberá as perguntas.
	 */
	QuestionarioTurma questionario;
	
	/**
	 * As perguntas a serem adicionadas ao questionário.
	 */
	List <PerguntaQuestionarioTurma> perguntas;
	
	public MovimentoAdicionarPerguntasDoBanco (QuestionarioTurma questionario, List <PerguntaQuestionarioTurma> perguntas){
		this.questionario = questionario;
		this.perguntas = perguntas;
		
		setCodMovimento(SigaaListaComando.ADICIONAR_PERGUNTAS_DO_BANCO);
	}

	public QuestionarioTurma getQuestionario() {
		return questionario;
	}

	public void setQuestionario(QuestionarioTurma questionario) {
		this.questionario = questionario;
	}

	public List<PerguntaQuestionarioTurma> getPerguntas() {
		return perguntas;
	}

	public void setPerguntas(List<PerguntaQuestionarioTurma> perguntas) {
		this.perguntas = perguntas;
	}
}