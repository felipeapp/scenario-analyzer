package br.ufrn.ppgsc.scenario.analyzer.cstatic.util;

import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ClassData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ComponentData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.MethodData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.PerformanceData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ReliabilityData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ScenarioData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.SecurityData;

public interface FactoryDataElement {

	public ClassData createClassData();

	public ComponentData createComponentData();

	public MethodData createMethodData();

	public PerformanceData createPerformanceData();

	public ScenarioData createScenarioData();

	public SecurityData createSecurityData();
	
	public ReliabilityData createReliabilityData();

}
