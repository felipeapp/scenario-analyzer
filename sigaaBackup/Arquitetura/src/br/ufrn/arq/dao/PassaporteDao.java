/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 17/01/2007
 */
package br.ufrn.arq.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.dominio.PassaporteLogon;

/**
 * DAO para buscas associadas à entidade Passaporte.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class PassaporteDao extends GenericSharedDBDao {

	@SuppressWarnings("unchecked")
	public PassaporteLogon findPassaporte(String login, int sistemaAlvo) throws DAOException {

		Criteria c = getSession().createCriteria(PassaporteLogon.class);
		c.add(Expression.eq("login", login));
		c.add(Expression.gt("validade", System.currentTimeMillis()));
		c.add(Expression.eq("sistemaAlvo", sistemaAlvo));
		c.addOrder(Order.desc("hora"));

		List<PassaporteLogon> lista = c.list();
		if (lista.size() > 0) {
			return lista.get(0);
		} else {
			return null;
		}

	}

}
