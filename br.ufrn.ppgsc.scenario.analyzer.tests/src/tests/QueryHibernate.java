package tests;

import java.util.ArrayList;
import java.util.Date;

import junit.framework.TestCase;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.criterion.Expression;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeNode;

@Component
public class QueryHibernate extends TestCase {

	private Session s = new AnnotationConfiguration()
			.configure("sa_hibernate.cfg.xml").buildSessionFactory()
			.openSession();

	public Session getSession() {
		return s;
	}
	
	public static void main(String[] args) {
		QueryHibernate o = new QueryHibernate();

		o.testB();
		o.testA();
	}

	private QueryHibernate() {

	}

	public void testA() {
		testB();
		C();
		new QueryHibernate();
	}

	private int testB() {
		C();

		SQLQuery sqlq = s.createSQLQuery("select member from node where id = :id and member like '%test%' and constructor = :cons");
		
		sqlq.setLong("id", 2);
		sqlq.setBoolean("cons", false);
		sqlq.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
		
		sqlq.uniqueResult();
		
		Query hqlq = s.createQuery("from node where id between 1 and :id and memberSignature like '%test%' and isConstructor = :flag");
		Query hqlq2 = s.createQuery("from scenario where date > :dataAtual and id not in (:col_i)");
		Query hqlq3 = s.createQuery("from scenario where date > :dataAtual and name not in (:col_s)");

		ArrayList<Long> li = new ArrayList<Long>();
		li.add(20L);
		li.add(19L);
		li.add(21L);
		
		ArrayList<String> ls = new ArrayList<String>();
		ls.add("teste");
		ls.add("sql");
		ls.add("aspectj");
		
		hqlq.setLong("id", 100);
		hqlq.setBoolean("flag", false);
		
		hqlq2.setDate("dataAtual", new Date());
		hqlq2.setParameterList("col_i", li);
		hqlq2.list();
		
		hqlq3.setDate("dataAtual", new Date());
		hqlq3.setParameterList("col_s", ls);
		hqlq3.list();
		
		for (Object o : hqlq.list()) {
			RuntimeNode node = (RuntimeNode) o;
			System.out.println("Node: " + node.getId() + " | " + node.getMemberSignature());
		}
		
		return 0;
	}

	public void C() {
		Criteria c = s.createCriteria(RuntimeNode.class);
		
		c.add(Expression.eq("id", 2L));
		c.add(Expression.ilike("memberSignature", "%test%"));
		c.add(Expression.eq("isConstructor", Boolean.TRUE));
		c.list();

		try {
			new JdbcTemplate().queryForLong("select time from node where id = ? and constructor = ?", 2, false, TesteDB.class);
		} catch (IllegalArgumentException e) {
			System.out.println("@@1: " + e.getMessage());
		}
	
		try {
			new JdbcTemplate().queryForObject(
					"select * from node where id = ? and constructor = ?",
					new Object[] { 2, false }, TesteDB.class);
		} catch (IllegalArgumentException e) {
			System.out.println("@@2: " + e.getMessage());
		}
		
		try {
			new JdbcTemplate().queryForObject(
					"select * from node where id = ? and constructor = ?",
					TesteDB.class, 2, false);
		} catch (IllegalArgumentException e) {
			System.out.println("@@3: " + e.getMessage());
		}
		
		try {
			new JdbcTemplate().queryForObject(
					"select * from node where id = ? and constructor = ?",
					TesteDB.class, new Object[] { 2, false });
		} catch (IllegalArgumentException e) {
			System.out.println("@@4: " + e.getMessage());
		}

		try {
			D();
		} catch (ArithmeticException e) {
			System.out.println("Exceção de D em C!");
		}
	}

	public void D() {
		System.out.println("Aqui em D!");
		System.out.println(1 / 0);
	}
	
//	public Map<String, Double> getExecutionTimeAverageOfMembers() {
//		Map<String, Double> result = new HashMap<String, Double>();
//
//		Session s = getSession();
//
//		SQLQuery query = s.createSQLQuery("select node.member signature, avg(node.time) average"
//				+ " from node where node.time <> -1 group by signature order by signature");
//
//		query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
//
//		query.addScalar("signature", StringType.class);
//		query.addScalar("average", DoubleType.class);
//
//		for (Object o : query.list()) {
//			Map<?, ?> elem = (Map<?, ?>) o;
//			result.put(elem.get("signature").toString(), (Double) elem.get("average"));
//		}
//
//		return result;
//	}

}
