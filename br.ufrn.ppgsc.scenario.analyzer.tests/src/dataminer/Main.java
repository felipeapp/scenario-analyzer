package dataminer;

import java.io.IOException;

import br.ufrn.ppgsc.scenario.analyzer.dataminer.AnalyzerMinerRunnable;

public class Main {

	public static void main(String[] args) throws IOException {
		new AnalyzerMinerRunnable("analyzer_miner.properties").run();
	}

}
