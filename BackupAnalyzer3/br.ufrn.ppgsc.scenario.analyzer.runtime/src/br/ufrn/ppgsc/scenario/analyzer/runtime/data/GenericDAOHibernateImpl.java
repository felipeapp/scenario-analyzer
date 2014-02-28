package br.ufrn.ppgsc.scenario.analyzer.runtime.data;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public class GenericDAOHibernateImpl<T extends Serializable> extends GenericDAO<T> {

	@Override
	public T persist(T instance) {
		Session s = getSession();

		s.beginTransaction();
		s.persist(instance);
		s.getTransaction().commit();

		return instance;
	}

	@Override
	public T read(Class<T> clazz, long id) {
		return (T) getSession().get(clazz, id);
	}

	@Override
	public List<T> readAll(Class<T> clazz) {
		return getSession().createQuery("from " + clazz.getName()).list();
	}

}
