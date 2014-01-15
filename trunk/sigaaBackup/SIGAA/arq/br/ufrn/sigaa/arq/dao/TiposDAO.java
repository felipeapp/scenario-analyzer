/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 26/07/2006
 * 
 */

package br.ufrn.sigaa.arq.dao;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;

import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;

/**
 * @author Andre M Dantas
 *
 */

public class TiposDAO extends GenericDAOImpl {

	public TiposDAO() {
		super(Sistema.SIGAA);
		daoName = "TiposDAO";
	}
	
	@SuppressWarnings("unchecked")
	public Collection<PersistDB> findByDescricao(String descricao, Class<?> classe) throws DAOException {
		Criteria c = getSession().createCriteria(classe);
		c.add(Expression.like("descricao", descricao, MatchMode.ANYWHERE));
		c.addOrder(Order.asc("descricao"));
		return c.list();
	}

	@SuppressWarnings("unchecked")
	public Collection<PersistDB> findByNome(String nome, Class<?> classe) throws DAOException {
		Criteria c = getSession().createCriteria(classe);
		c.add(Expression.like("nome", nome, MatchMode.ANYWHERE));
		c.addOrder(Order.asc("nome"));
		return c.list();
	}

}
