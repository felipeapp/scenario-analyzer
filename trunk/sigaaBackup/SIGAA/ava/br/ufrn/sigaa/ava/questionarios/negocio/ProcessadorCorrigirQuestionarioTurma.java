/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/01/2011
 *
 */
package br.ufrn.sigaa.ava.questionarios.negocio;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ava.questionario.dao.QuestionarioTurmaDao;
import br.ufrn.sigaa.ava.questionarios.dominio.ConjuntoRespostasQuestionarioAluno;
import br.ufrn.sigaa.ava.questionarios.dominio.EnvioRespostasQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.RespostaPerguntaQuestionarioTurma;

/**
 * Processador que salva a corre��o de uma resposta enviada para um question�rio da turma virtual.
 * 
 * @author Fred_Castro
 *
 */
public class ProcessadorCorrigirQuestionarioTurma extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException  {
		
		MovimentoCorrigirQuestionarioTurma pMov = (MovimentoCorrigirQuestionarioTurma) mov;
		
		EnvioRespostasQuestionarioTurma resposta = pMov.getResposta();
		
		QuestionarioTurmaDao dao = null;
		
		try {
			dao = getDAO(QuestionarioTurmaDao.class, pMov);
			
			// Salva todas as dissertativas cuja corre��o foi informada
			for (RespostaPerguntaQuestionarioTurma r : resposta.getRespostas())
				if (r.getPergunta().isDissertativa())
					dao.updateFields(RespostaPerguntaQuestionarioTurma.class, r.getId(),
							new String [] { "correcaoDissertativa","porcentagemNota" },
							new Object [] { r.getCorrecaoDissertativa(), r.getPorcentagemNota() }
					);
			
			// Salva a nova nota do aluno, indicando se ainda h� respostas dissertativas pendentes de corre��o.
			dao.updateFields(EnvioRespostasQuestionarioTurma.class, resposta.getId(),
					new String [] {"porcentagem", "dissertativasPendentes"},
					new Object [] { resposta.getPorcentagem(), resposta.isDissertativasPendentes()
			});
			
			// Salva a nova nota do conjunto de resposta do question�rio
			dao.updateField(ConjuntoRespostasQuestionarioAluno.class, resposta.getConjuntoRespostas().getId(), "porcentagem", resposta.getConjuntoRespostas().getPorcentagem());
			
			// Desmarca o question�rio para o docente saber que tem que publicar as notas novamente.
			dao.updateField(QuestionarioTurma.class, resposta.getQuestionario().getId(), "notasPublicadas", false);
			
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
