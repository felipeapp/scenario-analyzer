package tests;

import junit.framework.TestCase;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.criterion.Expression;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class QueryHibernate extends TestCase {

	private Session s = new AnnotationConfiguration()
			.configure("hibernate.cfg.1.xml").buildSessionFactory()
			.openSession();

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
		Criteria c = s.createCriteria(QueryHibernate.class);
		c.add(Expression.eq("grupoAtividade.id", 2));
		c.add(Expression.ilike("nome", "bla%"));
		c.add(Expression.eq("ativo", Boolean.TRUE));
		System.out.println("Criteria toString: " + c.toString());
		c.list();

		try {
			new JdbcTemplate().query("select id from JdbcTemplate",
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
