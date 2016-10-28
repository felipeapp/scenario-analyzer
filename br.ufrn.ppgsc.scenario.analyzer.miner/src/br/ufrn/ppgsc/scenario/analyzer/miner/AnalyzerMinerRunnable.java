package br.ufrn.ppgsc.scenario.analyzer.miner;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public final class AnalyzerMinerRunnable {

	public enum MinerType {
		Database, Repository, Both
	}

	public static void startAnalyzerMiner() throws IOException {
		String date = SystemMetadataUtil.getInstance().getStringProperty("date");
		String type = SystemMetadataUtil.getInstance().getStringProperty("mining_type");
		
		if (type.equals(MinerType.Database.name()) || type.equals(MinerType.Both.name()))
			date = new AnalyzerMinerByScenarioDBRunnable().run();

		if (type.equals(MinerType.Repository.name()) || type.equals(MinerType.Both.name()))
			new AnalyzerMinerByScenarioRepositoryRunnable(date).run();
	}
	
	public static void main(String[] args) {
		// Configuração básica para o log4j
		BasicConfigurator.configure();

		@SuppressWarnings("unchecked")
		List<Logger> loggers = Collections.list(LogManager.getCurrentLoggers());
		loggers.add(LogManager.getRootLogger());
		for (Logger logger : loggers)
			logger.setLevel(Level.OFF);
		
		Logger.getLogger("br.ufrn.ppgsc.scenario.analyzer.miner").setLevel(Level.ALL);

		try {
			long start = System.currentTimeMillis();
			AnalyzerMinerRunnable.startAnalyzerMiner();
			System.out.println("Total time: " + (System.currentTimeMillis() - start) / 60000.0 + " min");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
