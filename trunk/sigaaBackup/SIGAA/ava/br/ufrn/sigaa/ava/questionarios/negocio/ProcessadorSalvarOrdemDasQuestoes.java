/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/01/2011
 *
 */
package br.ufrn.sigaa.ava.questionarios.negocio;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.ava.questionarios.dominio.PerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;

/**
 * Processador que salva a ordem das perguntas do questionário
 * 
 * @author Fred_Castro
 *
 */
public class ProcessadorSalvarOrdemDasQuestoes extends AbstractProcessador {

	public Object execute(Movimento mov) throws DAOException  {

		MovimentoCadastro pMov = (MovimentoCadastro) mov;
		
		QuestionarioTurma q = pMov.getObjMovimentado();
		
		GenericDAO dao = null;
		
		try {
			dao = getGenericDAO(pMov);
			
			for (PerguntaQuestionarioTurma p : q.getPerguntas())
				dao.updateField (PerguntaQuestionarioTurma.class, p.getId(), "ordem", p.getOrdem());
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return null;

	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
