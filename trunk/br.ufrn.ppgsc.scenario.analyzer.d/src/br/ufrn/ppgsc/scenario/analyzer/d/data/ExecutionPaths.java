package br.ufrn.ppgsc.scenario.analyzer.d.data;

import java.util.ArrayList;
import java.util.List;

public class ExecutionPaths {

	private static final ExecutionPaths instance = new ExecutionPaths();
	
	private List<RuntimeCallGraph> cg_list;

	private ExecutionPaths() {
		cg_list = new ArrayList<RuntimeCallGraph>();
	}

	public static ExecutionPaths getInstance() {
		return instance;
	}
	
	public void addRuntimeCallGraph(RuntimeCallGraph cg) {
		cg_list.add(cg);
	}

	public RuntimeCallGraph[] getAllRuntimeCallGraph() {
		return cg_list.toArray(new RuntimeCallGraph[0]);
	}
	
	public RuntimeCallGraph[] getRuntimeCallGraphsByScenarioName(String scenario_name) {
		List<RuntimeCallGraph> list = new ArrayList<RuntimeCallGraph>();
		
		for (RuntimeCallGraph cg : cg_list)
			if (cg.getScenarioName().equals(scenario_name))
				list.add(cg);
		
		return list.toArray(new RuntimeCallGraph[0]);
	}

}