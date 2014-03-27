package br.ufrn.ppgsc.scenario.analyzer.miner.db;

import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemPropertiesUtil;

public abstract class DatabaseRelease {

	private static GenericDB databasev1;
	private static GenericDB databasev2;

	public static GenericDB getDatabasev1() {
		if (databasev1 == null)
			databasev1 = new DatabaseService(SystemPropertiesUtil.getInstance().getStringProperty("database_v1")).getGenericDB();

		return databasev1;
	}

	public static GenericDB getDatabasev2() {
		if (databasev2 == null)
			databasev2 = new DatabaseService(SystemPropertiesUtil.getInstance().getStringProperty("database_v2")).getGenericDB();

		return databasev2;
	}

}
