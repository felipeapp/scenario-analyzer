/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 30/10/2009
 */
package br.ufrn.sigaa.ava.questionarios.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.questionarios.dominio.AlternativaPerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.PerguntaQuestionarioTurma;

/**
 * Movimento que indica uma pergunta para ser salva e suas alternativas a serem removidas.
 * 
 * @author Fred de Castro
 *
 */

public class MovimentoSalvarPerguntaQuestionarioTurma extends AbstractMovimentoAdapter {
	
	/**
	 * A pergunta a ser salva.
	 */
	PerguntaQuestionarioTurma pergunta;
	
	/**
	 * A lista de alternativas já salvas a serem removidas.
	 */
	List <AlternativaPerguntaQuestionarioTurma> alternativasRemover;
	
	/**
	 * Indica se é para adicionar a pergunta também a uma categoria.
	 */
	private boolean adicionarEmCategoria;
	
	public MovimentoSalvarPerguntaQuestionarioTurma (PerguntaQuestionarioTurma pergunta, List <AlternativaPerguntaQuestionarioTurma> alternativasRemover, boolean adicionarEmCategoria){
		this.pergunta = pergunta;
		this.alternativasRemover = alternativasRemover;
		this.adicionarEmCategoria = adicionarEmCategoria;
		
		setCodMovimento(SigaaListaComando.SALVAR_PERGUNTA_QUESTIONARIO_TURMA);
	}

	public PerguntaQuestionarioTurma getPergunta() {
		return pergunta;
	}

	public void setPergunta(PerguntaQuestionarioTurma pergunta) {
		this.pergunta = pergunta;
	}

	public List<AlternativaPerguntaQuestionarioTurma> getAlternativasRemover() {
		return alternativasRemover;
	}

	public void setAlternativasRemover(
			List<AlternativaPerguntaQuestionarioTurma> alternativasRemover) {
		this.alternativasRemover = alternativasRemover;
	}

	public boolean isAdicionarEmCategoria() {
		return adicionarEmCategoria;
	}

	public void setAdicionarEmCategoria(boolean adicionarEmCategoria) {
		this.adicionarEmCategoria = adicionarEmCategoria;
	}

}