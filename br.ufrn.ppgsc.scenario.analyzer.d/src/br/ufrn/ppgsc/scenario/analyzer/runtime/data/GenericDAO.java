package br.ufrn.ppgsc.scenario.analyzer.runtime.data;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public abstract class GenericDAO<T extends Serializable> {

	public abstract T create(T newInstance);

	public abstract T read(T entity);

	public abstract T read(long id);
	
	public abstract T read(Class<T> clazz, long id);

	public abstract List<T> readAll(Class<T> clazz);

	public abstract void update(T transientObject);

	public abstract void delete(T persistentObject);
	
	private static SessionFactory sf;
	private static Session s;

	public static synchronized Session getSession() {
		if (sf == null && s == null) {
			sf = new AnnotationConfiguration().configure("hibernate.cfg.xml").buildSessionFactory();
			s = sf.openSession();
		}
		
		return s;
	}

}
