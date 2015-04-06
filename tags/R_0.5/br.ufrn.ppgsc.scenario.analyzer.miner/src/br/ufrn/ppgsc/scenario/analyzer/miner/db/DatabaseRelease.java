package br.ufrn.ppgsc.scenario.analyzer.miner.db;

import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public abstract class DatabaseRelease {

	private static GenericDB databasev1;
	private static GenericDB databasev2;

	public static GenericDB getDatabasev1() {
		if (databasev1 == null)
			databasev1 = new DatabaseService(SystemMetadataUtil.getInstance().getStringProperty("database_r1")).getGenericDB();

		return databasev1;
	}

	public static GenericDB getDatabasev2() {
		if (databasev2 == null)
			databasev2 = new DatabaseService(SystemMetadataUtil.getInstance().getStringProperty("database_r2")).getGenericDB();

		return databasev2;
	}

}
