/**
 *
 */
package br.ufrn.comum.dao;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.correios.Localidade;
import br.ufrn.comum.dominio.correios.Logradouro;

/**
 * DAO para consultas a base dos correios
 *
 * @author Ricardo Wendell
 *
 */
@SuppressWarnings("unchecked")
public class BaseCorreiosDAO extends GenericSharedDBDao {

	public Logradouro findLogradouroByCep(long cep) throws DAOException {
		Criteria c = getSession().createCriteria(Logradouro.class);
		c.add(Expression.eq("cep", cep));
		c.setMaxResults(1);

		return (Logradouro) c.uniqueResult();
	}

	public Localidade findLocalidadeByCep(long cep) throws DAOException {
		Criteria c = getSession().createCriteria(Localidade.class);
		c.add(Expression.eq("cep", cep));
		c.setMaxResults(1);

		return (Localidade) c.uniqueResult();
	}
	

	public Collection<Localidade> findLocalidadesByUF(String uf) throws DAOException {
		Criteria c = getSession().createCriteria(Localidade.class);
		c.add(Expression.eq("uf", uf));
		c.addOrder(Order.asc("nome"));

		return c.list();
	}
	
	/**
	 * Retorna os municípios começando com determinado nome
	 * 
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	public Collection<Localidade> findLocalidadesByNome(String nome) throws DAOException {
		Criteria c = getSession().createCriteria(Localidade.class);
		c.add(Expression.like("nomeAscii", StringUtils.toAscii(nome.toUpperCase()) + "%"));
		c.addOrder(Order.asc("nome"));

		return c.list();
	}

}
