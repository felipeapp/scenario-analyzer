package br.ufrn.ppgsc.scenario.analyzer.dataminer;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

public abstract class GenericDAO<T extends Serializable> {

	public abstract T read(Class<T> clazz, long id);

	public abstract List<T> readAll(Class<T> clazz);
	
	public abstract Map<String, Double> getExecutionTimeAverage();

	private SessionFactory sessionFactory;
	private Session session;
	private String hibernateCfg;

	public GenericDAO(String hibernateCfg) {
		this.hibernateCfg = hibernateCfg;
		sessionFactory = new AnnotationConfiguration().configure(hibernateCfg).buildSessionFactory();
		session = sessionFactory.openSession();
	}

	public String getHibernateCfg() {
		return hibernateCfg;
	}

	public Session getSession() {
		return session;
	}

}
