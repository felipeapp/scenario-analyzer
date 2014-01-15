package br.ufrn.sigaa.arq.dao.questionario;

import java.util.Collection;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.questionario.dominio.PerguntaQuestionario;
import br.ufrn.sigaa.questionario.dominio.Questionario;

/**
 * DAO responsável pelas perguntas dos Questionários.
 *  
 * @author Jean Guerethes
 */
public class PerguntaQuestionarioDao extends GenericSigaaDAO {

	/**
	 * Retorna todas as perguntas do Questionário passado.
	 */
	@SuppressWarnings("unchecked")
	public Collection<PerguntaQuestionario> findAllPerguntasQuestionario(Questionario questionario) throws DAOException{
		Query q = getSession().createQuery("from PerguntaQuestionario pq " +
				" where pq.questionario = :questionario and ativo = trueValue() order by ordem");
		q.setInteger("questionario", questionario.getId());
		return q.list();
	}
	
}