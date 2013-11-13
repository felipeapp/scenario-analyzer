package br.ufrn.ppgsc.scenario.analyzer.d.data;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

public class GenericDAOHibernateImpl<T extends Serializable> extends GenericDAO<T> {

	@Override
	public T create(T newInstance) {
		Session s = getSession();
		s.beginTransaction();
		s.persist(newInstance);
		s.getTransaction().commit();
		return newInstance;
	}

	@Override
	public T read(T entity) {
		return null;
	}

	@Override
	public T read(long id) {
		return null;
	}
	
	@Override
	public T read(Class<T> clazz, long id) {
		return (T) getSession().get(clazz, id);
	}

	@Override
	public void update(T transientObject) {
		// TODO Auto-generated method stub
	}

	@Override
	public void delete(T persistentObject) {
		// TODO Auto-generated method stub
	}

	@Override
	public List<T> readAll(Class<T> clazz) {
		List<T> objects = null;

		try {
			Session s = getSession();
			Query query = s.createQuery("from " + clazz.getName());
			s.beginTransaction();
			objects = query.list();
			s.getTransaction().commit();
		} catch (HibernateException e) {
			e.printStackTrace();
		}

		return objects;
	}

}
