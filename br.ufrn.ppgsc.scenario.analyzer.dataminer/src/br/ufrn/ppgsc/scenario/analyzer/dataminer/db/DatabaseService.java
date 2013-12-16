package br.ufrn.ppgsc.scenario.analyzer.dataminer.db;

public class DatabaseService {

	private GenericDB dao;

	public DatabaseService(String hibernateCfg) {
		dao = new GenericDBHibernateImpl(hibernateCfg);
	}

	public GenericDB getGenericDB() {
		return dao;
	}

}
