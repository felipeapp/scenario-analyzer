package br.ufrn.sigaa.ensino_rede.dao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino_rede.dominio.AlteracaoStatusDiscenteAssociado;
/**
 * Dao utilizado para as consultas da entidade AlteracaoStatusDiscenteAssociado.
 * 
 * @author Jeferson Queiroga
 *
 */
public class AlteracaoStatusDiscenteAssociadoDao extends GenericSigaaDAO {
	
	/** Retorna a ultima alteração de status do discente.
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public AlteracaoStatusDiscenteAssociado findUltimaAlteracaoByDiscente(int discente) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(AlteracaoStatusDiscenteAssociado.class);
			c.add(Expression.eq("discente.id", discente));
			c.addOrder(Order.desc("data"));
			c.setMaxResults(1);
			return (AlteracaoStatusDiscenteAssociado) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
}
