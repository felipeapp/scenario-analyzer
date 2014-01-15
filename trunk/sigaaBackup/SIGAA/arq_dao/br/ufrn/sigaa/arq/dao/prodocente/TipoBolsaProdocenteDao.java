/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '23/11/2007'
 *
 */
package br.ufrn.sigaa.arq.dao.prodocente;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoBolsaProdocente;

/**
 * @author Ricardo Wendell
 *
 */
public class TipoBolsaProdocenteDao extends GenericSigaaDAO {

	/**
	 * Busca todos os tipos de bolsa de produtividade com status ativo
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<TipoBolsaProdocente> findAllProdutividade() throws DAOException {
		try {
			Criteria c = getSession().createCriteria(TipoBolsaProdocente.class);
			c.add(Expression.eq("ativo", true));
			c.add(Expression.eq("produtividade", true));
			c.addOrder( Order.asc("descricao"));

			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

}
