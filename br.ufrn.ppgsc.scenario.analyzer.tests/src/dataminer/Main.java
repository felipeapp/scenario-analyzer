package dataminer;

import java.io.IOException;

import br.ufrn.ppgsc.scenario.analyzer.miner.AnalyzerMinerRunnable;
import br.ufrn.ppgsc.scenario.analyzer.miner.AnalyzerMinerRunnable.MinerType;

public class Main {

	public static void main(String[] args) throws IOException {
		AnalyzerMinerRunnable.startAnalyzerMiner(new PathTransformerSIGAA(),
				MinerType.MinerRepository);
	}

}
