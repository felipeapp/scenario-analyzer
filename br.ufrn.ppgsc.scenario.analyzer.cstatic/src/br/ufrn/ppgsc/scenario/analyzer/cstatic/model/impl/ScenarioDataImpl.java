package br.ufrn.ppgsc.scenario.analyzer.cstatic.model.impl;

import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.MethodData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ScenarioData;

public class ScenarioDataImpl implements ScenarioData {

	private String name;
	private MethodData startMethod;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MethodData getStartMethod() {
		return startMethod;
	}

	public void setStartMethod(MethodData startMethod) {
		this.startMethod = startMethod;
	}

}
