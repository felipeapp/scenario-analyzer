package br.ufrn.ppgsc.scenario.analyzer.miner.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import br.ufrn.ppgsc.scenario.analyzer.runtime.data.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.runtime.data.RuntimeScenario;

public class GenericDBHibernateImpl extends GenericDB {

	public GenericDBHibernateImpl(String hibernateCfg) {
		super(hibernateCfg);
	}

	@Override
	public <T> T read(Class<T> clazz, long id) {
		return (T) getSession().get(clazz, id);
	}

	/* O cenário falhou se seu ponto de entrada falhou, ou seja, tempo = -1
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
		query.addScalar("total", Hibernate.INTEGER);

		return (int) query.uniqueResult();
	}
	
	@Override
	public Map<String, Double> getExecutionTimeAverage() {
		Map<String, Double> result = new HashMap<String, Double>();

		Session s = getSession();

		SQLQuery query = s.createSQLQuery("select node.member signature, avg(node.time) average"
				+ " from node where node.time <> -1 group by node.member order by signature");

		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);

		query.addScalar("signature", Hibernate.STRING);
		query.addScalar("average", Hibernate.DOUBLE);

		for (Object o : query.list()) {
			Map<?, ?> elem = (Map<?, ?>) o;
			result.put(elem.get("signature").toString(), (Double) elem.get("average"));
		}

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

}
