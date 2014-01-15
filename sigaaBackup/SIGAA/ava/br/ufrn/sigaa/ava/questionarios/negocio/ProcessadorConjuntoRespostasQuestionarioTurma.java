package br.ufrn.sigaa.ava.questionarios.negocio;

import java.util.ArrayList;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.questionario.dao.QuestionarioTurmaDao;
import br.ufrn.sigaa.ava.questionarios.dominio.ConjuntoRespostasQuestionarioAluno;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;

/**
 * Processador que realiza uma alteração um conjunto de respostas.
 * 
 * @author Diego Jácome
 *
 */
public class ProcessadorConjuntoRespostasQuestionarioTurma extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException  {
		
		return removerConjuntos(mov);

	}
	
	private Object removerConjuntos(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoCadastro rMov = (MovimentoCadastro) mov;
		
		ConjuntoRespostasQuestionarioAluno conjunto = rMov.getObjMovimentado();
		
		QuestionarioTurmaDao dao = null;
		AvaliacaoDao aDao = null;
		
		try {
			dao = getDAO(QuestionarioTurmaDao.class, rMov);		
			aDao = getDAO(AvaliacaoDao.class, rMov);	
			
			ArrayList<Avaliacao> avaliacoes = aDao.findByConjuntoRespostaQuestionario(conjunto);

			// Desmarca o questionário para o docente saber que tem que publicar as notas novamente.
			dao.updateField(QuestionarioTurma.class, conjunto.getQuestionario().getId(), "notasPublicadas", false);
			if ( avaliacoes != null )
				for ( Avaliacao avaliacao : avaliacoes )
					dao.updateField(Avaliacao.class, avaliacao.getId(), "nota", null);
			
			dao.update(conjunto);
		} finally {
			if (dao != null)
				dao.close();
		}
		return null;
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}
	
}
