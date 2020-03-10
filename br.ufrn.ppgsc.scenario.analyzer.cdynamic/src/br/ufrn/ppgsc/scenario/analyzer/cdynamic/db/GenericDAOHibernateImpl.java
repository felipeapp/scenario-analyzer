package br.ufrn.ppgsc.scenario.analyzer.cdynamic.db;

import java.io.Serializable;
import java.util.List;

import javax.persistence.TypedQuery;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class GenericDAOHibernateImpl<T extends Serializable> implements GenericDAO<T> {

	private static Session s;

	static {
		SessionFactory sf = null;

		try {
			String filepath;
			String jboss_home = System.getProperty("jboss.home.url");

			if (jboss_home != null)
				filepath = jboss_home + "/server/default/deploy/sa_hibernate.cfg.xml";
			else
				filepath = "sa_hibernate.cfg.xml";

			sf = new Configuration().configure(filepath).buildSessionFactory();
		} catch (Exception e) {
			e.printStackTrace();
		}

		s = sf.openSession();
	}

	@Override
	public void clearSession() {
		synchronized (s) {
			try {
				s.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public T save(T instance) {
		synchronized (s) {
			Transaction tx = null;

			try {
				tx = s.beginTransaction();
				System.out.println("Saving " + instance.toString());
				s.save(instance);
				System.out.println("Commiting " + instance.toString());
				tx.commit();
			} catch (Exception e) {
				if (tx != null)
					tx.rollback();

				e.printStackTrace();
			}

			return instance;
		}
	}

	@Override
	public T read(Class<T> clazz, long id) {
		synchronized (s) {
			Object object = s.get(clazz, id);
			return clazz.cast(object);
		}
	}

	@Override
	public List<T> readAll(Class<T> clazz) {
		synchronized (s) {
			TypedQuery<T> query = s.createQuery("from " + clazz.getName(), clazz);
			return query.getResultList();
		}
	}

}
