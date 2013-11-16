package br.ufrn.ppgsc.scenario.analyzer.d.util;

import br.ufrn.ppgsc.scenario.analyzer.d.data.Execution;

public abstract class RuntimeUtil {

	private static final Execution execution = new Execution();

	public static Execution getCurrentExecution() {
		return execution;
	}

}
