package br.ufrn.ppgsc.scenario.analyzer.dataminer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

public class GenericDAOHibernateImpl<T extends Serializable> extends
		GenericDAO<T> {

	public GenericDAOHibernateImpl(String hibernateCfg) {
		super(hibernateCfg);
	}

	@Override
	public T read(Class<T> clazz, long id) {
		return (T) getSession().get(clazz, id);
	}

	@Override
	public List<T> readAll(Class<T> clazz) {
		List<T> objects = null;

		try {
			Session s = getSession();
			Query query = s.createQuery("from " + clazz.getName());
			s.beginTransaction();
			objects = query.list();
			s.getTransaction().commit();
		} catch (HibernateException e) {
			e.printStackTrace();
		}

		return objects;
	}

	@Override
	public Map<String, Double> getExecutionTimeAverage() {
		Map<String, Double> result = new HashMap<String, Double>();

		try {
			Session s = getSession();

			SQLQuery query = s.createSQLQuery("select node.member signature, avg(node.time) average"
					+ " from node group by node.member order by signature;");

			query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);

			query.addScalar("signature", Hibernate.STRING);
			query.addScalar("average", Hibernate.DOUBLE);

			s.beginTransaction();

			List data = query.list();

			for (Object o : data) {
				Map elem = (Map) o;
				result.put(elem.get("signature").toString(), (double) elem.get("average"));
			}

			s.getTransaction().commit();
		} catch (HibernateException e) {
			e.printStackTrace();
		}

		return result;
	}

}
