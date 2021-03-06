package br.ufrn.ppgsc.scenario.analyzer.cdynamic.db;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class GenericDAOHibernateImpl<T extends Serializable> implements GenericDAO<T> {

	// Session is a static attribute
	private static Session s;

	// Only one session for all application
	public GenericDAOHibernateImpl() {
		if (s == null) {
			SessionFactory sf = new Configuration().configure("sa_hibernate.cfg.xml").buildSessionFactory();
			s = sf.openSession();
		}
	}

	@Override
	public void clearSession() {
		try {
			s.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public T save(T instance) {
		Transaction tx = null;

		try {
			tx = s.beginTransaction();
			System.out.println("Saving " + instance.toString());
			s.save(instance);
			System.out.println("Commiting " + instance.toString());
			tx.commit();
		} catch (RuntimeException e) {
			if (tx != null)
				tx.rollback();

			e.printStackTrace();
		}

		return instance;
	}

	@Override
	public T read(Class<T> clazz, long id) {
		Object object = s.get(clazz, id);
		return clazz.cast(object);
	}

	@Override
	public List<T> readAll(Class<T> clazz) {
		Query query = s.createQuery("from " + clazz.getName());

		@SuppressWarnings("unchecked")
		List<T> list = query.list();

		return list;
	}

}
