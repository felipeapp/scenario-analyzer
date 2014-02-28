package br.ufrn.ppgsc.scenario.analyzer.runtime.data;

import java.io.Serializable;
import java.util.Iterator;

public class DatabaseService<T extends Serializable> {

	private GenericDAO<T> getGenericDAO() {
		GenericDAO<T> dao = new GenericDAOHibernateImpl<T>();
		return dao;
	}

	public synchronized static void saveResults(Execution e) {
		synchronized (e) {
			GenericDAO<RuntimeScenario> dao = new DatabaseService<RuntimeScenario>().getGenericDAO();

			Iterator<RuntimeScenario> it = e.getScenarios().iterator();

			while (it.hasNext()) {
				RuntimeScenario rs = it.next();

				/*
				 * O cenário será da thread que terminou será persistido e
				 * removido da lista. A remoção não é realmente necessária, mas
				 * é feita mesmo assim para liberar memória. Para um sistema
				 * grande isso pode fazer diferença.
				 */
				if (rs.getThreadId() == Thread.currentThread().getId()) {
					it.remove();
					dao.persist(rs);
				}
			}
		}
	}

}
