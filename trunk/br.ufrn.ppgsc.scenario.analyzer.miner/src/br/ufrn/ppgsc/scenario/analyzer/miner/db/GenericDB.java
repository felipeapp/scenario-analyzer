package br.ufrn.ppgsc.scenario.analyzer.miner.db;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeScenario;

public abstract class GenericDB {

	public abstract <T> T read(Class<T> clazz, long id);

	public abstract List<RuntimeScenario> getScenariosFailed();

	public abstract int getNumberOfMethodExecution(String signature);

	public abstract Map<String, Double> getExecutionTimeAverage();

	public abstract List<RuntimeNode> getFailedNodes(RuntimeScenario scenario);

	public abstract List<String> getScenariosByMember(String signature);

	public abstract Set<String> getImpactedNodes(String signarute);

	private Session s;

	public GenericDB(String hibernateCfg) {
		SessionFactory sf = new Configuration().configure(hibernateCfg).buildSessionFactory();
		s = sf.openSession();
	}

	public Session getSession() {
		return s;
	}

}
