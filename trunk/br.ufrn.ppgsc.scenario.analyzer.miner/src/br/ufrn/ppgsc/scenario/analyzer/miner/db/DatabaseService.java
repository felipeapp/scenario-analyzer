package br.ufrn.ppgsc.scenario.analyzer.miner.db;

public class DatabaseService {

	private GenericDB dao;

	public DatabaseService(String hibernateCfg) {
		dao = new GenericDBHibernateImpl(hibernateCfg);
	}

	public GenericDB getGenericDB() {
		return dao;
	}

}
