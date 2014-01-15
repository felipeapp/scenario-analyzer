/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/01/2011
 * 
 */
package br.ufrn.sigaa.ava.questionario.dao;

import java.util.List;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.questionarios.dominio.PerguntaQuestionarioTurma;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;

/**
 * DAO que gerencia as perguntas de questionários da turma virtual. 
 * 
 * @author Fred_Castro
 *
 */
public class PerguntaQuestionarioTurmaDao extends GenericSigaaDAO {

	/**
	 * Encontra todas as perguntas ativas do questionário passado
	 * 
	 * @param questionario
	 * @return
	 * @throws DAOException
	 */
	public List <PerguntaQuestionarioTurma> findAllPerguntasQuestionario (QuestionarioTurma questionario) throws DAOException{
		@SuppressWarnings("unchecked")
		List <PerguntaQuestionarioTurma> rs = getSession().createQuery("select pq from PerguntaQuestionarioTurma pq " +
				" where questionarioTurma.id = " + questionario.getId() + " and ativo = trueValue() order by ordem").list();

		return rs;
	}
}