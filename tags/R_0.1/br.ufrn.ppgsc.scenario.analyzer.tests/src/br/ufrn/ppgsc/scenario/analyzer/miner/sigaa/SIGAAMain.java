package br.ufrn.ppgsc.scenario.analyzer.miner.sigaa;

import java.io.IOException;

import br.ufrn.ppgsc.scenario.analyzer.miner.AnalyzerMinerRunnable;
import br.ufrn.ppgsc.scenario.analyzer.miner.AnalyzerMinerRunnable.MinerType;

public class SIGAAMain {

	public static void main(String[] args) throws IOException {
		AnalyzerMinerRunnable.startAnalyzerMiner(new PathTransformerSIGAA(), MinerType.MinerDB);
	}

}
