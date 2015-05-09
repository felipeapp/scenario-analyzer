package br.ufrn.ppgsc.scenario.analyzer.cdynamic.db;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public abstract class GenericDAO<T extends Serializable> {

	public abstract T persist(T instance);

	public abstract T read(Class<T> clazz, long id);

	public abstract List<T> readAll(Class<T> clazz);

	private static Session s;

	public static Session getSession() {
		if (s == null) {
			/*
			 *  AnnotationConfiguration is deprecated. We should use Configuration
			 *  AnnotationConfiguration is being used because of SIGAA
			 */
			SessionFactory sf = new AnnotationConfiguration().configure("hibernate.cfg.xml").buildSessionFactory();
			s = sf.openSession();
		}

		return s;
	}

}