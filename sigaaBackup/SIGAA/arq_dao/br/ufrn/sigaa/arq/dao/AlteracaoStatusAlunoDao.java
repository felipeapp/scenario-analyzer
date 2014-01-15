/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/11/2006
 *
 */
package br.ufrn.sigaa.arq.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.dominio.AlteracaoStatusAluno;

/**
 * Classe respons�vel por consultas espec�ficas � Altera��o de Status do Discente 
 * @author Andre M Dantas
 *
 */
public class AlteracaoStatusAlunoDao extends GenericSigaaDAO {

	/** Retorna a ultima altera��o de status do discente.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public AlteracaoStatusAluno findUltimaAlteracaoByDiscente(int discente) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(AlteracaoStatusAluno.class);
			c.add(Expression.eq("discente.id", discente));
			c.addOrder(Order.desc("data"));
			c.setMaxResults(1);
			return (AlteracaoStatusAluno) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	

}
