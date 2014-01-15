/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/10/2011
 *
 */
package br.ufrn.sigaa.ava.questionarios.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ava.questionario.dao.QuestionarioTurmaDao;
import br.ufrn.sigaa.ava.questionarios.dominio.ConjuntoRespostasQuestionarioAluno;
import br.ufrn.sigaa.ava.questionarios.dominio.EnvioRespostasQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.PerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;

/**
 * Processador que remove uma pergunta de um questionário, recorrigindo os envios dos discentes.
 * 
 * @author Fred_Castro
 *
 */
public class ProcessadorRemoverPerguntaQuestionarioTurma extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastro pMov = (MovimentoCadastro) mov;
		
		QuestionarioTurmaDao qtDAO = null;
		
		try {
			qtDAO = getDAO(QuestionarioTurmaDao.class, mov);
			
			PerguntaQuestionarioTurma pergunta = (PerguntaQuestionarioTurma) pMov.getObjMovimentado();
	
			pergunta = qtDAO.findAndFetch(pergunta.getId(), PerguntaQuestionarioTurma.class, "questionarioTurma");
			
			QuestionarioTurma questionario = pergunta.getQuestionarioTurma();
			// Desativa a pergunta
			qtDAO.updateField(PerguntaQuestionarioTurma.class, pergunta.getId(), "ativo", false);
			
			// Caso a pergunta seja de um questionário, busca os envios das respostas para efetuar o recálculo.
			if (pergunta.getQuestionarioTurma() != null){
				
				// Recalcula as tentativas
				List <EnvioRespostasQuestionarioTurma> envios = (List<EnvioRespostasQuestionarioTurma>) qtDAO.findByExactField(EnvioRespostasQuestionarioTurma.class, "questionario.id", pergunta.getQuestionarioTurma().getId());
				for (EnvioRespostasQuestionarioTurma e : envios){
					e.calcularNota();
					qtDAO.updateField(EnvioRespostasQuestionarioTurma.class, e.getId(), "porcentagem", e.getPorcentagem());
				}
			
				// Recalcula os conjuntos
				List <ConjuntoRespostasQuestionarioAluno> conjuntos = qtDAO.findConjuntoRespostasByQuestionario(questionario);
				for ( ConjuntoRespostasQuestionarioAluno c : conjuntos ){
					c.setQuestionario(questionario);
					c.calcularNotas();
					qtDAO.updateField(ConjuntoRespostasQuestionarioAluno.class, c.getId(), "porcentagem", c.getPorcentagem());				
				}
			}		
		} finally {
			if ( qtDAO != null )
				qtDAO.close();
		}
		return null;
	}
	
	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro pMov = (MovimentoCadastro) mov;
		
		QuestionarioTurma questionario = (QuestionarioTurma) pMov.getObjMovimentado();
		
		if (questionario.isPossuiNota() && questionario.getUnidade() < 1)
			throw new NegocioException ("Já que este questionário valerá nota, por favor, selecione uma unidade.");
			
	}

}
