package br.ufrn.ppgsc.scenario.analyzer.cdynamic.db;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

public class GenericDAOHibernateImpl<T extends Serializable> implements GenericDAO<T> {

	private static Session s;

	private synchronized Session getSession() {
		if (s == null) {
			/*
			 * AnnotationConfiguration is deprecated. We should use
			 * Configuration. AnnotationConfiguration should be used with SIGAA
			 */
			SessionFactory sf = new AnnotationConfiguration().configure("hibernate.cfg.xml").buildSessionFactory();
			s = sf.openSession();

			// This is the best way, but it does not work with old sinfo libs.
			// SessionFactory sf = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
			// s = sf.openSession();
		}

		return s;
	}

	@Override
	public synchronized void clearSession() {
		try {
			getSession().clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized T save(T instance) {
		Session s = getSession();
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
	public synchronized T read(Class<T> clazz, long id) {
		Object object = getSession().get(clazz, id);
		return clazz.cast(object);
	}

	@Override
	public synchronized List<T> readAll(Class<T> clazz) {
		Query query = getSession().createQuery("from " + clazz.getName());

		@SuppressWarnings("unchecked")
		List<T> list = query.list();

		return list;
	}

}
