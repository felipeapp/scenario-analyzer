package br.ufrn.ppgsc.scenario.analyzer.miner.db;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import br.ufrn.ppgsc.scenario.analyzer.runtime.data.RuntimeScenario;

public abstract class GenericDB {

	public abstract <T> T read(Class<T> clazz, long id);
	
	public abstract List<RuntimeScenario> getScenariosFailed();

	public abstract int getNumberOfMethodExecution(String signature);
	
	public abstract Map<String, Double> getExecutionTimeAverage();

	private SessionFactory sessionFactory;
	private Session session;

	public GenericDB(String hibernateCfg) {
		sessionFactory = new AnnotationConfiguration().configure(hibernateCfg).buildSessionFactory();
		session = sessionFactory.openSession();
	}

	public Session getSession() {
		return session;
	}

}
