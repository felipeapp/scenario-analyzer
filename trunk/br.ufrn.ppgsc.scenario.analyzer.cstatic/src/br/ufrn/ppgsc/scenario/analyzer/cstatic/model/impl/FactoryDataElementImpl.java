package br.ufrn.ppgsc.scenario.analyzer.cstatic.model.impl;

import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ClassData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ComponentData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.MethodData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.PerformanceData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ReliabilityData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ScenarioData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.SecurityData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.util.FactoryDataElement;

public class FactoryDataElementImpl implements FactoryDataElement {

	public ClassData createClassData() {
		return new ClassDataImpl();
	}

	public ComponentData createComponentData() {
		return new ComponentDataImpl();
	}

	public MethodData createMethodData() {
		return new MethodDataImpl();
	}

	public PerformanceData createPerformanceData() {
		return new PerformanceDataImpl();
	}

	public ScenarioData createScenarioData() {
		return new ScenarioDataImpl();
	}

	public SecurityData createSecurityData() {
		return new SecurityDataImpl();
	}
	
	public ReliabilityData createReliabilityData() {
		return new ReliabilityDataImpl();
	}

}
