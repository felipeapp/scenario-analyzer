package br.ufrn.ppgsc.scenario.analyzer.miner;

import java.io.IOException;

import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public final class AnalyzerMinerRunnable {

	public enum MinerType {
		Database, Repository, Both
	}

	public static void startAnalyzerMiner() throws IOException {
		String date = SystemMetadataUtil.getInstance().getStringProperty("date");
		String type = SystemMetadataUtil.getInstance().getStringProperty("mining_type");
		
		if (type.equals(MinerType.Database.name()) || type.equals(MinerType.Both.name()))
			date = new AnalyzerMinerDBRunnable().run();

		if (type.equals(MinerType.Repository.name()) || type.equals(MinerType.Both.name()))
			new AnalyzerMinerRepositoryRunnable(date).run();
	}
	
	public static void main(String[] args) {
		try {
			AnalyzerMinerRunnable.startAnalyzerMiner();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
