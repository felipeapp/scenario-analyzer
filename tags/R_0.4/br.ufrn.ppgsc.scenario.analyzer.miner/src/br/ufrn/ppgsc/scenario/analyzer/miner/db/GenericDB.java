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

	public abstract List<RuntimeScenario> getFailedScenarios();

	public abstract int countGeneralMethodExecution(String signature);
	
	public abstract int countMethodExecutionByScenario(String scenario, String signature);

	public abstract Map<String, Double> getExecutionTimeAverageOfMembers();
	
	public abstract Map<String, Double> getExecutionTimeAverageOfScenarios();

	public abstract List<RuntimeNode> getFailedNodes(RuntimeScenario scenario);

	public abstract List<String> getScenariosByMember(String signature);

	public abstract Set<String> getImpactedNodes(String signarute);
	
	public abstract double[] getAllExecutionTimeByMember(String signature);
	
	public abstract double[] getAllExecutionTimeByScenario(String sname);

	private Session s;

	public GenericDB(String hibernateCfg) {
		SessionFactory sf = new Configuration().configure(hibernateCfg).buildSessionFactory();
		s = sf.openSession();
	}

	public Session getSession() {
		return s;
	}

}
