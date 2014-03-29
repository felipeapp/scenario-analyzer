package br.ufrn.ppgsc.scenario.analyzer.runtime.db;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

// Esta classe usa libs do Hibernate 4
public abstract class GenericDAO<T extends Serializable> {

	public abstract T persist(T instance);

	public abstract T read(Class<T> clazz, long id);

	public abstract List<T> readAll(Class<T> clazz);

	private static Session s;

	public static Session getSession() {
		if (s == null) {
			Configuration configuration = new Configuration()
					.configure("hibernate.cfg.xml");

			ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
					.applySettings(configuration.getProperties())
					.buildServiceRegistry();

			SessionFactory sf = configuration
					.buildSessionFactory(serviceRegistry);

			s = sf.openSession();
		}

		return s;
	}

}
