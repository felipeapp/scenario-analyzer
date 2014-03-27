package br.ufrn.ppgsc.scenario.analyzer.runtime.data;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public abstract class GenericDAO<T extends Serializable> {

	public abstract T persist(T instance);

	public abstract T read(Class<T> clazz, long id);

	public abstract List<T> readAll(Class<T> clazz);

	private static SessionFactory sf;
	private static Session s;

	public static Session getSession() {
		if (sf == null && s == null) {
			sf = new AnnotationConfiguration().configure("hibernate.cfg.xml").buildSessionFactory();
			s = sf.openSession();
		}

		return s;
	}

}
