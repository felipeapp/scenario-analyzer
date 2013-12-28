package br.ufrn.ppgsc.scenario.analyzer.runtime.data;

import java.io.Serializable;
import java.util.Iterator;

public class DatabaseService<T extends Serializable> {

	private GenericDAO<T> getGenericDAO() {
		GenericDAO<T> dao = new GenericDAOHibernateImpl<T>();
		return dao;
	}

	public static synchronized void saveResults(Execution e) {
		GenericDAO<RuntimeScenario> dao = new DatabaseService<RuntimeScenario>().getGenericDAO();

		Iterator<RuntimeScenario> it = e.getScenarios().iterator();

		while (it.hasNext()) {
			RuntimeScenario rs = it.next();

			if (rs.getThreadId() == Thread.currentThread().getId()) {
				it.remove();
				dao.persist(rs);
			}
		}
	}

}
