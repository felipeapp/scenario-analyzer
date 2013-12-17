package br.ufrn.ppgsc.scenario.analyzer.runtime.data;

import java.io.Serializable;

public class DatabaseService<T extends Serializable> {

	private GenericDAO<T> getGenericDAO() {
		GenericDAO<T> dao = new GenericDAOHibernateImpl<T>();
		return dao;
	}

	public static synchronized void saveResults(Execution e) {
		GenericDAO<Execution> dao = new DatabaseService<Execution>().getGenericDAO();
		dao.create(e);
	}

}
