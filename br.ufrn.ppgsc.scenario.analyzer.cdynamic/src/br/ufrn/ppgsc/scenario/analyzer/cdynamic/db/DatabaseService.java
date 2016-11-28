package br.ufrn.ppgsc.scenario.analyzer.cdynamic.db;

import java.io.Serializable;
import java.util.Iterator;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.SystemExecution;

public class DatabaseService<T extends Serializable> {

	private GenericDAO<T> getGenericDAO() {
		GenericDAO<T> dao = new GenericDAOHibernateImpl<T>();
		// GenericDAO<T> dao = new GenericDAOFileImpl<T>();
		return dao;
	}

	public synchronized static void saveResults(SystemExecution e) {
		synchronized (e) {
			final GenericDAO<RuntimeScenario> dao = new DatabaseService<RuntimeScenario>().getGenericDAO();

			Iterator<RuntimeScenario> it = e.getScenarios().iterator();

			while (it.hasNext()) {
				final RuntimeScenario rs = it.next();

				/*
				 * O cenário será da thread que terminou será persistido e
				 * removido da lista. A remoção não é realmente necessária, mas
				 * é feita mesmo assim para liberar memória. Para um sistema
				 * grande isso pode fazer diferença.
				 */
				if (rs.getThreadId() == Thread.currentThread().getId()) {
					it.remove();

					new Thread(new Runnable() {
						public void run() {
							long t1 = System.currentTimeMillis();
							dao.save(rs);
							dao.clearSession();
							long t2 = System.currentTimeMillis();
							System.out.println("# Save time: " + (t2 - t1) + "ms");
						}
					}).start();
				}
			}
		}
	}

}
