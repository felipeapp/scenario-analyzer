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
	public List<RuntimeScenario> getScenariosFailed() {
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
	public int getNumberOfMethodExecution(String signature) {
		SQLQuery query = getSession().createSQLQuery(
				"select count(node.id) total from node where member = :signature");

		query.setString("signature", signature);
		query.addScalar("total", IntegerType.INSTANCE);

		return (int) query.uniqueResult();
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

		SQLQuery query = s.createSQLQuery("select scenario.name sname, avg(node.time) saverage from scenario inner join node"
				+ " on scenario.root_id = node.id and node.time <> -1 group by sname order by sname;");

		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);

		query.addScalar("sname", StringType.INSTANCE);
		query.addScalar("saverage", DoubleType.INSTANCE);

		for (Object o : query.list()) {
			Map<?, ?> elem = (Map<?, ?>) o;
			result.put(elem.get("sname").toString(), (Double) elem.get("saverage"));
		}

		return result;
	}
	
	@Override
	public List<String> getScenariosByMember(String signature) {
		List<String> result = new ArrayList<String>();

		Session s = getSession();

		SQLQuery query = s.createSQLQuery("select distinct scenario.name sname from node_scenario, node, scenario"
				+ " where node_scenario.node_id = node.id and node_scenario.scenario_id = scenario.id and"
				+ " node.member = :signature order by scenario.name");

		query.setString("signature", signature);
		query.addScalar("sname", StringType.INSTANCE);

		for (Object o : query.list())
			result.add((String) o);

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
