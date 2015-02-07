package br.ufrn.ppgsc.scenario.analyzer.miner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.DatabaseRelease;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDB;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerCollectionUtil;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerReportUtil;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerStatistical;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerStatistical.StatElement;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerStatistical.Tests;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public final class AnalyzerMinerDBRunnable {

	private GenericDB database_v1;
	private GenericDB database_v2;
	
	private double performance_rate;
	private double significance_level;
	private String system_id;
	
	private String strdate;
	
	public AnalyzerMinerDBRunnable() throws IOException {
		SystemMetadataUtil properties = SystemMetadataUtil.getInstance();
		
		system_id = properties.getStringProperty("system_id");
		performance_rate = properties.getDoubleProperty("performance_rate");
		significance_level = properties.getDoubleProperty("significance_level");
		
		database_v1 = DatabaseRelease.getDatabasev1();
		database_v2 = DatabaseRelease.getDatabasev2();
		
		strdate = new SimpleDateFormat("yyyy-MM-dd_HH'h'mm'min'").format(new Date());
	}
	
	private Map<RuntimeScenario, List<RuntimeNode>> buildMapOfFailedScenarios(
			Collection<RuntimeScenario> failed_scenarios, Map<String, Integer> node_signatures,
			GenericDB db) {
		int i = 0;
		Map<RuntimeScenario, List<RuntimeNode>> map = new HashMap<RuntimeScenario, List<RuntimeNode>>();
		
		for (RuntimeScenario s : failed_scenarios) {
			long start = System.currentTimeMillis();
			
			System.out.print("Building map for scenario " + s.getId() + ", " + s.getName() + ", " + ++i + "/" + failed_scenarios.size());
			
			List<RuntimeNode> failed_nodes = db.getFailedNodes(s);
			
			map.put(s, failed_nodes);
			
			for (RuntimeNode n : failed_nodes) {
				Integer count = node_signatures.get(n.getMemberSignature());
				
				if (count == null)
					count = 0;
				
				node_signatures.put(n.getMemberSignature(), count + 1);
			}
			
			System.out.println(", " + (int)((System.currentTimeMillis() - start) / 1000.0) + " seconds");
		}
		
		return map;
	}
	
	private String getFileName(String partial_name) {
		return "miner_log/" + system_id + "_" + partial_name + "_" + strdate + ".txt";
	}
	
	public String run() throws FileNotFoundException {
		// Object responsible for the calculation of the statistical tests
		AnalyzerStatistical as = new AnalyzerStatistical();
		
		// Here, we start the treatment of the failed scenarios and methods
		System.out.println("Getting scenarios and means for release 1...");
		Map<String, Double> avg_scenarios_v1 = database_v1.getExecutionTimeAverageOfScenarios();
		System.out.println("\tTotal = " + avg_scenarios_v1.size());
		
		System.out.println("Getting scenarios and means for release 2...");
		Map<String, Double> avg_scenarios_v2 = database_v2.getExecutionTimeAverageOfScenarios();
		System.out.println("\tTotal = " + avg_scenarios_v2.size());
		
		System.out.println("Determining removed scenarios...");
		Set<String> removed_scenarios = AnalyzerCollectionUtil.except(avg_scenarios_v1.keySet(), avg_scenarios_v2.keySet());
		System.out.println("\tTotal = " + removed_scenarios.size());
		
		System.out.println("Determining added scenarios...");
		Set<String> added_scenarios = AnalyzerCollectionUtil.except(avg_scenarios_v2.keySet(), avg_scenarios_v1.keySet());
		System.out.println("\tTotal = " + added_scenarios.size());
		
		System.out.println("Calculating statistical tests for common scenarios...");
		Map<String, StatElement> scenario_results = as.executeStatisticalTests(avg_scenarios_v1, avg_scenarios_v2, RuntimeScenario.class);
		
		// Save scenarios in separated files
		AnalyzerReportUtil.saveReport("# Scenarios executed in both releases", getFileName("kept_scenarios"), scenario_results.values(), 0, 0);
		AnalyzerReportUtil.saveReport("# Scenarios executed in the first release, but not in the second", getFileName("removed_scenarios"), avg_scenarios_v1, removed_scenarios, 0, 0);
		AnalyzerReportUtil.saveReport("# Scenarios executed in the second release, but not in the first", getFileName("added_scenarios"), avg_scenarios_v2, added_scenarios, 0, 0);
		
		// Save scenarios according their execution time variation measured by the performance rate
		AnalyzerReportUtil.saveReport("# Degradated scenarios (Rate method)", getFileName("pr_degraded_scenarios"), AnalyzerCollectionUtil.degradatedRate(scenario_results.values(), performance_rate), performance_rate, 0);
		AnalyzerReportUtil.saveReport("# Optimized scenarios (Rate method)", getFileName("pr_optimized_scenarios"), AnalyzerCollectionUtil.optimizedRate(scenario_results.values(), performance_rate), performance_rate, 0);
		AnalyzerReportUtil.saveReport("# Unchanged scenarios (Rate method)", getFileName("pr_unchanged_scenarios"), AnalyzerCollectionUtil.unchangedRate(scenario_results.values(), performance_rate), performance_rate, 0);
		
		// Save scenarios according their execution time variation measured by the TTest
		AnalyzerReportUtil.saveReport("# Degradated scenarios (TTest P-Value)", getFileName("pt_degraded_scenarios"), AnalyzerCollectionUtil.degradatedPValue(scenario_results.values(), significance_level, Tests.TTest), 0, significance_level);
		AnalyzerReportUtil.saveReport("# Optimized scenarios (TTest P-Value)", getFileName("pt_optimized_scenarios"), AnalyzerCollectionUtil.optimizedPValue(scenario_results.values(), significance_level, Tests.TTest), 0, significance_level);
		AnalyzerReportUtil.saveReport("# Unchanged scenarios (TTest P-Value)", getFileName("pt_unchanged_scenarios"), AnalyzerCollectionUtil.unchangedPValue(scenario_results.values(), significance_level, Tests.TTest), 0, significance_level);
		
		// Save scenarios according their execution time variation measured by the UTest
		AnalyzerReportUtil.saveReport("# Degradated scenarios (UTest P-Value)", getFileName("pu_degraded_scenarios"), AnalyzerCollectionUtil.degradatedPValue(scenario_results.values(), significance_level, Tests.UTest), 0, significance_level);
		AnalyzerReportUtil.saveReport("# Optimized scenarios (UTest P-Value)", getFileName("pu_optimized_scenarios"), AnalyzerCollectionUtil.optimizedPValue(scenario_results.values(), significance_level, Tests.UTest), 0, significance_level);
		AnalyzerReportUtil.saveReport("# Unchanged scenarios (UTest P-Value)", getFileName("pu_unchanged_scenarios"), AnalyzerCollectionUtil.unchangedPValue(scenario_results.values(), significance_level, Tests.UTest), 0, significance_level);
		
		System.out.println("-------------------------------------------------------------------");
		
		// Here, we start the treatment of the methods
		System.out.println("Getting methods and means for release 1...");
		Map<String, Double> avg_members_v1 = database_v1.getExecutionTimeAverageOfMembers();
		System.out.println("\tTotal = " + avg_members_v1.size());
		
		System.out.println("Getting methods and means for release 2...");
		Map<String, Double> avg_members_v2 = database_v2.getExecutionTimeAverageOfMembers();
		System.out.println("\tTotal = " + avg_members_v2.size());

		System.out.println("Determining removed methods...");
		Set<String> removed_methods = AnalyzerCollectionUtil.except(avg_members_v1.keySet(), avg_members_v2.keySet());
		System.out.println("\tTotal = " + removed_methods.size());
		
		System.out.println("Determining added methods...");
		Set<String> added_methods = AnalyzerCollectionUtil.except(avg_members_v2.keySet(), avg_members_v1.keySet());
		System.out.println("\tTotal = " + added_methods.size());
		
		System.out.println("Calculating statistical tests for common methods...");
		Map<String, StatElement> method_results = as.executeStatisticalTests(avg_members_v1, avg_members_v2, RuntimeNode.class);
		
		// Save methods in separated files
		AnalyzerReportUtil.saveReport("# Methods executed in both releases", getFileName("kept_methods"), method_results.values(), 0, 0);
		AnalyzerReportUtil.saveReport("# Methods executed in the first release, but not in the second", getFileName("removed_methods"), avg_members_v1, removed_methods, 0, 0);
		AnalyzerReportUtil.saveReport("# Methods executed in the second release, but not in the first", getFileName("added_methods"), avg_members_v2, added_methods, 0, 0);
		
		// Save methods according their execution time variation measured by the performance rate
		AnalyzerReportUtil.saveReport("# Degradated methods (Rate method)", getFileName("pr_degraded_methods"), AnalyzerCollectionUtil.degradatedRate(method_results.values(), performance_rate), performance_rate, 0);
		AnalyzerReportUtil.saveReport("# Optimized methods (Rate method)", getFileName("pr_optimized_methods"), AnalyzerCollectionUtil.optimizedRate(method_results.values(), performance_rate), performance_rate, 0);
		AnalyzerReportUtil.saveReport("# Unchanged methods (Rate method)", getFileName("pr_unchanged_methods"), AnalyzerCollectionUtil.unchangedRate(method_results.values(), performance_rate), performance_rate, 0);
		
		// Save scenarios according their execution time variation measured by the TTest
		AnalyzerReportUtil.saveReport("# Degradated methods (TTest P-Value)", getFileName("pt_degraded_methods"), AnalyzerCollectionUtil.degradatedPValue(method_results.values(), significance_level, Tests.TTest), 0, significance_level);
		AnalyzerReportUtil.saveReport("# Optimized methods (TTest P-Value)", getFileName("pt_optimized_methods"), AnalyzerCollectionUtil.optimizedPValue(method_results.values(), significance_level, Tests.TTest), 0, significance_level);
		AnalyzerReportUtil.saveReport("# Unchanged methods (TTest P-Value)", getFileName("pt_unchanged_methods"), AnalyzerCollectionUtil.unchangedPValue(method_results.values(), significance_level, Tests.TTest), 0, significance_level);
		
		// Save scenarios according their execution time variation measured by the UTest
		AnalyzerReportUtil.saveReport("# Degradated methods (UTest P-Value)", getFileName("pu_degraded_methods"), AnalyzerCollectionUtil.degradatedPValue(method_results.values(), significance_level, Tests.UTest), 0, significance_level);
		AnalyzerReportUtil.saveReport("# Optimized methods (UTest P-Value)", getFileName("pu_optimized_methods"), AnalyzerCollectionUtil.optimizedPValue(method_results.values(), significance_level, Tests.UTest), 0, significance_level);
		AnalyzerReportUtil.saveReport("# Unchanged methods (UTest P-Value)", getFileName("pu_unchanged_methods"), AnalyzerCollectionUtil.unchangedPValue(method_results.values(), significance_level, Tests.UTest), 0, significance_level);
		
		System.out.println("-------------------------------------------------------------------");
		
		// Here, we start the treatment of the failed scenarios and methods
		System.out.println("Getting failed scenarios for release 1...");
		List<RuntimeScenario> failed_scenarios_v1 = database_v1.getFailedScenarios();
		System.out.println("\tTotal = " + failed_scenarios_v1.size());
		
		System.out.println("Getting failed scenarios for release 2...");
		List<RuntimeScenario> failed_scenarios_v2 = database_v2.getFailedScenarios();
		System.out.println("\tTotal = " + failed_scenarios_v2.size());
		
		Map<String, Integer> failed_methods_v1 = new HashMap<String, Integer>();
		Map<String, Integer> failed_methods_v2 = new HashMap<String, Integer>();
		
		Map<RuntimeScenario, List<RuntimeNode>> failed_scenario_to_node_v1 = buildMapOfFailedScenarios(failed_scenarios_v1, failed_methods_v1, database_v1);
		AnalyzerReportUtil.saveReport("# Release 1 failed scenarios", getFileName("r1_failed_scenarios"), failed_scenario_to_node_v1, failed_methods_v1, 1);
		
		Map<RuntimeScenario, List<RuntimeNode>> failed_scenario_to_node_v2 = buildMapOfFailedScenarios(failed_scenarios_v2, failed_methods_v2, database_v2);
		AnalyzerReportUtil.saveReport("# Release 2 failed scenarios", getFileName("r2_failed_scenarios"), failed_scenario_to_node_v2, failed_methods_v2, 2);
		
		System.out.println("Getting failed methods for release 1...");
		Set<String> failed_methods_only_v1 = AnalyzerCollectionUtil.except(failed_methods_v1.keySet(), failed_methods_v2.keySet());
		System.out.println("\tTotal = " + failed_methods_only_v1.size());
		
		System.out.println("Getting failed methods for release 1...");
		Set<String> failed_methods_only_v2 = AnalyzerCollectionUtil.except(failed_methods_v2.keySet(), failed_methods_v1.keySet());
		System.out.println("\tTotal = " + failed_methods_only_v2.size());
		
		System.out.println("Getting failed methods for both releases...");
		Set<String> failed_methods_both = AnalyzerCollectionUtil.intersect(failed_methods_v1.keySet(), failed_methods_v2.keySet());
		System.out.println("\tTotal = " + failed_methods_both.size());
		
		AnalyzerReportUtil.saveReport("# Methods that have failed only in release 1", getFileName("failed_methods_only_v1"),failed_methods_only_v1);
		AnalyzerReportUtil.saveReport("# Methods that have failed only in release 2", getFileName("failed_methods_only_v2"), failed_methods_only_v2);
		AnalyzerReportUtil.saveReport("# Methods that have failed only in both releases", getFileName("failed_methods_both"), failed_methods_both);
		
		return strdate;
	}

}
