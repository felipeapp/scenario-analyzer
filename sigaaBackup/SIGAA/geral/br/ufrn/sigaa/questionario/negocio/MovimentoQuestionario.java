/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Sep 8, 2008
 *
 */
package br.ufrn.sigaa.questionario.negocio;

import java.util.List;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.questionario.dominio.Alternativa;
import br.ufrn.sigaa.questionario.dominio.PerguntaQuestionario;

/**
 *
 * @author Victor Hugo
 */
public class MovimentoQuestionario extends MovimentoCadastro{

	private List<PerguntaQuestionario> perguntasRemover;
	private List<Alternativa> alternativasRemover;

	public List<PerguntaQuestionario> getPerguntasRemover() {
		return perguntasRemover;
	}
	public void setPerguntasRemover(List<PerguntaQuestionario> perguntasRemover) {
		this.perguntasRemover = perguntasRemover;
	}
	public List<Alternativa> getAlternativasRemover() {
		return alternativasRemover;
	}
	public void setAlternativasRemover(List<Alternativa> alternativasRemover) {
		this.alternativasRemover = alternativasRemover;
	}

}
