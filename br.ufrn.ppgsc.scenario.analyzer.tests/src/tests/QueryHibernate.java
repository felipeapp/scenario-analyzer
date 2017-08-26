package tests;

import junit.framework.TestCase;

import org.hibernate.Criteria;
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

		s.createSQLQuery("insert into X");
		s.createQuery("select id from execution");

		return 0;
	}

	public void C() {
		Criteria c = s.createCriteria(RuntimeNode.class);
		
		c.add(Expression.eq("id", 2L));
		c.add(Expression.ilike("memberSignature", "%test%"));
		c.add(Expression.eq("isConstructor", Boolean.TRUE));
		System.out.println("Criteria toString: " + c.toString());
		c.list();

		try {
			new JdbcTemplate().query("select id from scenario",
					new String[] { "felipe, alves" }, new TesteDB());
		} catch (Throwable t) {
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

}
