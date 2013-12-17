package br.ufrn.ppgsc.scenario.analyzer.miner;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class AnalyzerMinerRunnable {

	public enum MinerType {
		MinerDB, MinerRepository, Both
	}

	public static void startAnalyzerMiner(IPathTransformer o, MinerType type) throws IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream("analyzer_miner.properties"));

		String date = properties.getProperty("date");
		
		if (type == MinerType.MinerDB || type == MinerType.Both)
			date = new AnalyzerMinerDBRunnable(properties).run();

		if (type == MinerType.MinerRepository || type == MinerType.Both)
			new AnalyzerMinerRepositoryRunnable(properties, date, o).run();
	}

}
