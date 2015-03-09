package br.ufrn.ppgsc.scenario.analyzer.miner.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeScenario;

public class GenericDBHibernateImpl extends GenericDB {

	public GenericDBHibernateImpl(String hibernateCfg) {
		super(hibernateCfg);
	}
	
	@Override
	public <T> T read(Class<T> clazz, long id) {
		Object object = getSession().get(clazz, id);
		return clazz.cast(object);
	}

	/* 
	 * O cenário falhou se seu ponto de entrada falhou, ou seja, tempo = -1
	 * significando que a execução do ponto de entrada abortou.
	 */
	@Override
	public List<RuntimeScenario> getFailedScenarios() {
		List<RuntimeScenario> result = new ArrayList<RuntimeScenario>();
		
		Session s = getSession();

		SQLQuery query = s.createSQLQuery("select scenario.* from scenario inner join node"
				+ " on scenario.root_id = node.id and node.time = -1");
		
		query.addEntity(RuntimeScenario.class);
		
		for (Object o : query.list())
			result.add((RuntimeScenario) o);

		return result;
	}
	
	@Override
	public int countGeneralMethodExecution(String signature) {
		SQLQuery query = getSession().createSQLQuery(
				"select count(node.id) total from node where member = :signature");

		query.setString("signature", signature);
		query.addScalar("total", IntegerType.INSTANCE);

		return (int) query.uniqueResult();
	}
	
	private Map<String, Integer> cache_scenariosignature_to_total = new HashMap<String, Integer>();
	@Override
	public int countMethodExecutionByScenario(String scenario, String signature) {
		String key = scenario + signature;
		Integer total = cache_scenariosignature_to_total.get(key);
		
		if (total == null) {
			double t1 = System.currentTimeMillis();
			
			SQLQuery query = getSession().createSQLQuery(
				"select scenario.name sname, count(node.id) total from "
				+ "node inner join node_scenario on node.id = node_scenario.node_id "
				+ "inner join scenario on node_scenario.scenario_id = scenario.id "
				+ "where node.time <> -1 and node.member = :signature group by sname order by sname"
			);
			
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			query.setString("signature", signature);
			
			query.addScalar("sname", StringType.INSTANCE);
			query.addScalar("total", IntegerType.INSTANCE);
	
			for (Object o : query.list()) {
				Map<?, ?> elem = (Map<?, ?>) o;
				cache_scenariosignature_to_total.put(elem.get("sname").toString() + signature, (Integer) elem.get("total"));
			}
			
			total = cache_scenariosignature_to_total.get(key);
			if (total == null)
				total = 0;
			
			System.out.println("[countMethodExecutionByScenario] Time: " + (System.currentTimeMillis() - t1) + "ms");
		}
		else {
			System.out.println("[countMethodExecutionByScenario] Reused key: " + key);
		}
		
		return total;
	}
	
	@Override
	public Map<String, Double> getExecutionTimeAverageOfMembers() {
		Map<String, Double> result = new HashMap<String, Double>();

		Session s = getSession();

		SQLQuery query = s.createSQLQuery("select node.member signature, avg(node.time) average"
				+ " from node where node.time <> -1 group by signature order by signature");

		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);

		query.addScalar("signature", StringType.INSTANCE);
		query.addScalar("average", DoubleType.INSTANCE);

		for (Object o : query.list()) {
			Map<?, ?> elem = (Map<?, ?>) o;
			result.put(elem.get("signature").toString(), (Double) elem.get("average"));
		}

		return result;
	}
	
	@Override
	public Map<String, Double> getExecutionTimeAverageOfScenarios() {
		Map<String, Double> result = new HashMap<String, Double>();

		Session s = getSession();

		SQLQuery query = s.createSQLQuery("select scenario.name sname, avg(node.time) saverage"
				+ " from scenario inner join node on scenario.root_id = node.id and node.time <> -1"
				+ " group by sname order by sname");

		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);

		query.addScalar("sname", StringType.INSTANCE);
		query.addScalar("saverage", DoubleType.INSTANCE);

		for (Object o : query.list()) {
			Map<?, ?> elem = (Map<?, ?>) o;
			result.put(elem.get("sname").toString(), (Double) elem.get("saverage"));
		}

		return result;
	}
	
	private Map<String, List<String>> cache_signature_to_scenarios = new HashMap<String, List<String>>();
	@Override
	public List<String> getScenariosByMember(String signature) {
		List<String> scenarios = cache_signature_to_scenarios.get(signature);
		
		if (scenarios == null) {
			double t1 = System.currentTimeMillis();
			
			scenarios = new ArrayList<String>();
	
			SQLQuery query = getSession().createSQLQuery(
				"select scenario.name sname, count(node.id) total from "
				+ "node inner join node_scenario on node.id = node_scenario.node_id "
				+ "inner join scenario on node_scenario.scenario_id = scenario.id "
				+ "where node.time <> -1 and node.member = :signature group by sname order by sname"
			);
	
			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
			query.setString("signature", signature);
			
			query.addScalar("sname", StringType.INSTANCE);
			query.addScalar("total", IntegerType.INSTANCE);
	
			for (Object o : query.list()) {
				Map<?, ?> elem = (Map<?, ?>) o;
				cache_scenariosignature_to_total.put(elem.get("sname").toString() + signature, (Integer) elem.get("total"));
				scenarios.add(elem.get("sname").toString());
			}
			
			cache_signature_to_scenarios.put(signature, scenarios);
			System.out.println("[getScenariosByMember] Time: " + (System.currentTimeMillis() - t1) + "ms");
		}
		else {
			System.out.println("[getScenariosByMember] Reused key: " + signature);
		}

		return scenarios;
	}
	
	private Map<String, double[]> cache_signature_to_executions = new HashMap<String, double[]>();
	@Override
	public double[] getAllExecutionTimeByMember(String signature) {
		double[] result = cache_signature_to_executions.get(signature);
		
		if (result == null) {
			double t1 = System.currentTimeMillis();
			
			Session s = getSession();
	
			SQLQuery query = s.createSQLQuery("select time from node where time <> -1 and member = :signature");
	
			query.setString("signature", signature);
			query.addScalar("time", LongType.INSTANCE);
	
			List<?> rset = query.list();
			result = new double[rset.size()];
			
			for (int i = 0; i < result.length; i++)
				result[i] = (Long) rset.get(i);
			
			cache_signature_to_executions.put(signature, result);
			System.out.println("[getAllExecutionTimeByMember] Time: " + (System.currentTimeMillis() - t1) + "ms");
		}
		else {
			System.out.println("[getAllExecutionTimeByMember] Reused key: " + signature);
		}

		return result;
	}
	
	@Override
	public double[] getAllExecutionTimeByScenario(String sname) {
		Session s = getSession();

		SQLQuery query = s.createSQLQuery("select node.time stime from scenario inner join node"
				+ " on scenario.root_id = node.id and scenario.name = :sname and node.time <> -1");

		query.setString("sname", sname);
		query.addScalar("stime", LongType.INSTANCE);

		List<?> rset = query.list();
		double[] result = new double[rset.size()];
		
		for (int i = 0; i < result.length; i++)
			result[i] = (Long) rset.get(i);

		return result;
	}
	
	@Override
	public List<RuntimeNode> getFailedNodes(RuntimeScenario scenario) {
		List<RuntimeNode> result = new ArrayList<RuntimeNode>();
		
		Session s = getSession();

		SQLQuery query = s.createSQLQuery("select n.* from node_scenario inner join node n"
				+ " on node_scenario.node_id = n.id and node_scenario.scenario_id = :sid and"
				+ " n.time = -1 and (select count(id) from node where parent_id = n.id and time = -1) = 0"
				+ " order by n.id");
		
		query.setLong("sid", scenario.getId());
		query.addEntity(RuntimeNode.class);
		
		for (Object o : query.list())
			result.add((RuntimeNode) o);

		return result;
	}

	@Override
	public Set<String> getImpactedNodes(String signarute) {
		Set<String> result = new HashSet<String>();
		
		Session s = getSession();

		SQLQuery query = s.createSQLQuery("select n1.* from node n1 inner join node n2"
				+ " on n1.id = n2.parent_id and n2.member = :member order by n1.member");
		
		query.setString("member", signarute);
		query.addEntity(RuntimeNode.class);
		
		for (Object o : query.list())
			result.addAll(getImpactedNodes((RuntimeNode) o));

		return result;
	}
	
	private Set<String> getImpactedNodes(RuntimeNode root) {
		Set<String> result = new HashSet<String>();
		
		if (root.getParent() != null) {
			result.add(root.getParent().getMemberSignature());
			result.addAll(getImpactedNodes(root.getParent()));
		}
		
		return result;
	}

}
