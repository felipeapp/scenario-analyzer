/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 21/01/2008
 */
package br.ufrn.arq.dao;

import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.NonUniqueResultException;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;

import br.ufrn.arq.util.UFRNUtils;


/**
 * Classe que estende as funcionalidades do HibernateTemplate do Spring.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class HibernateTemplate extends org.springframework.orm.hibernate3.HibernateTemplate {

	public HibernateTemplate(SessionFactory sf) {
		super(sf);
	}
	
	public Object uniqueResult(String query) {
		List<?> lista = find(query);
		return getResult(lista);
	}
	
	public Object uniqueResult(String query, Object... params) {
		List<?> lista = find(query, params);
		return getResult(lista);
	}

	private Object getResult(List<?> lista) {
		if (isEmpty(lista)) return null;
		return lista.get(0);
	}

	public Object uniqueResult(DetachedCriteria criteria) {
		List<?> lista = findByCriteria(criteria, 0, 1);
		if (isEmpty(lista)) return null;
		if (lista.size() > 1) throw new NonUniqueResultException(lista.size());
		return lista.get(0);
	}

	public long count(DetachedCriteria criteria) {
		try {
			DetachedCriteria count = (DetachedCriteria) UFRNUtils.deepCopy(criteria);
			count.setProjection(Projections.rowCount());
			Number uniqueResult = (Number) uniqueResult(count);
			return uniqueResult.longValue();
		} catch (Exception e) {
			throw new HibernateException(e.getMessage(), e);
		}
	}
}
