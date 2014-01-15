/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '26/10/2010'
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.projetos.dominio.QuestionarioAvaliacao;

/**
 * Dao responsável por consultas relacionadas ao questionários de avaliações de projetos
 * @author geyson
 *
 */
public class QuestionarioAvaliacaoDao extends GenericSigaaDAO {

	/**
	 * Verifica se um grupo já esta ligada a algum questionario de avaliação.
	 * @param grupo
	 * @return
	 * @throws DAOException
	 */
	public QuestionarioAvaliacao findQuestionarioExistente(QuestionarioAvaliacao questionario) throws DAOException {

		String hql = " select q from ModeloAvaliacao ma " +
		" inner join ma.questionario q " +
		" where  ma.ativo = trueValue() " +
		" and q.id = " + questionario.getId() + " ";

		Query query = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		List<QuestionarioAvaliacao> lista = query.list();

		for (QuestionarioAvaliacao questionarioAvaliacao : lista) {
			if(questionarioAvaliacao != null)
				return questionarioAvaliacao;
		}

		return null;
	}
	
}
