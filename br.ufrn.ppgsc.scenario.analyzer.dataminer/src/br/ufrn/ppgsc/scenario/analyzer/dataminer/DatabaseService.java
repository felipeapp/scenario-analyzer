package br.ufrn.ppgsc.scenario.analyzer.dataminer;

import java.io.Serializable;

public class DatabaseService<T extends Serializable> {

	private GenericDAO<T> dao;

	public DatabaseService(String hibernateCfg) {
		dao = new GenericDAOHibernateImpl<T>(hibernateCfg);
	}

	public GenericDAO<T> getGenericDAO() {
		return dao;
	}

}
