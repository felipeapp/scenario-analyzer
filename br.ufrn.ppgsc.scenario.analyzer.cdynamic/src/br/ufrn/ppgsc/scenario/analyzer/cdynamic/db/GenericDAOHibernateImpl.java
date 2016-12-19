package br.ufrn.ppgsc.scenario.analyzer.cdynamic.db;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
//import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;

public class GenericDAOHibernateImpl<T extends Serializable> implements GenericDAO<T> {

	private static Session s;

	static {
		//file:/C:/development/jboss-5.1.0.GA-original-tuning/
		System.out.println("JBOSS HOME ###############:" + System.getProperty("jboss.home.url") + "/server/default/deploy/sa_hibernate.cfg.xml"); 

		SessionFactory sf = null;
		
		try {
			sf = new AnnotationConfiguration().configure(new URL(
					System.getProperty("jboss.home.url") + "/server/default/deploy/sa_hibernate.cfg.xml")).buildSessionFactory();
		} catch (MalformedURLException e) {
			sf = new AnnotationConfiguration().configure("sa_hibernate.cfg.xml").buildSessionFactory();
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
			} catch (RuntimeException e) {
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
			Query query = s.createQuery("from " + clazz.getName());

			@SuppressWarnings("unchecked")
			List<T> list = query.list();

			return list;
		}
	}

}
