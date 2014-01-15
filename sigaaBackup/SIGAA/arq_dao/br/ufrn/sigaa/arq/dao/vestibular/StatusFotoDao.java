package br.ufrn.sigaa.arq.dao.vestibular;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;

public class StatusFotoDao extends GenericSigaaDAO {
	
	/** Indica se já existe status de foto cadastrado com a mesma descrição.
	 * @param descricao
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean hasStatusMesmaDescricao(String descricao) throws HibernateException, DAOException {
		if (descricao == null) return false;
		String hql = "select count(*)" +
				" from StatusFoto status" +
				" where upper(descricao) = :descricao";
		Query q = getSession().createQuery(hql);
		q.setString("descricao", descricao.toUpperCase());
		long qtd = (Long) q.uniqueResult();
		return qtd > 0;
	}

}
