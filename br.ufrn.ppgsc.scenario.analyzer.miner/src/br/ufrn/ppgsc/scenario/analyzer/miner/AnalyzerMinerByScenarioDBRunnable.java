package br.ufrn.ppgsc.scenario.analyzer.miner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.miner.db.DatabaseRelease;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDB;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.DoubleStatElement;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.SimpleStatElement;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerCollectionUtil;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerCollectionUtil.Deviation;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerReportUtil;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerStatistical;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerStatistical.Tests;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public final class AnalyzerMinerByScenarioDBRunnable {

	private GenericDB database_v1;
	private GenericDB database_v2;
	
	private double alpha_significance_level;
	private String system_id;
	
	private String strdate;
	
	public AnalyzerMinerByScenarioDBRunnable() throws IOException {
		SystemMetadataUtil properties = SystemMetadataUtil.getInstance();
		
		system_id = properties.getStringProperty("system_id");
		alpha_significance_level = properties.getDoubleProperty("alpha_significance_level");
		
		database_v1 = DatabaseRelease.getDatabasev1();
		database_v2 = DatabaseRelease.getDatabasev2();
		
		strdate = new SimpleDateFormat("yyyy-MM-dd_HH'h'mm'min'").format(new Date());
	}
	
	private String getFileName(String partial_name) {
		return "reports/degradation_analysis/" + system_id + "_" + partial_name + "_" + strdate + ".txt";
	}
	
	public String run() throws FileNotFoundException {
		// Object responsible for the calculation of the statistical tests
		AnalyzerStatistical as = new AnalyzerStatistical();
		
		// Here, we start the treatment of the failed scenarios and methods
		System.out.println("Getting scenarios and means for release 1...");
		Map<String, SimpleStatElement> scenarios_v1 = database_v1.getSimpleStatOfScenarios();
		System.out.println("\tTotal = " + scenarios_v1.size());
		
		System.out.println("Getting scenarios and means for release 2...");
		Map<String, SimpleStatElement> scenarios_v2 = database_v2.getSimpleStatOfScenarios();
		System.out.println("\tTotal = " + scenarios_v2.size());
		
		System.out.println("Determining removed scenarios...");
		Set<String> removed_scenarios = AnalyzerCollectionUtil.except(scenarios_v1.keySet(), scenarios_v2.keySet());
		System.out.println("\tTotal = " + removed_scenarios.size());
		
		System.out.println("Determining added scenarios...");
		Set<String> added_scenarios = AnalyzerCollectionUtil.except(scenarios_v2.keySet(), scenarios_v1.keySet());
		System.out.println("\tTotal = " + added_scenarios.size());
		
		System.out.println("Determining kept scenarios...");
		Set<String> kept_scenarios = AnalyzerCollectionUtil.intersect(scenarios_v1.keySet(), scenarios_v2.keySet());
		System.out.println("\tTotal = " + kept_scenarios.size());
		
		System.out.println("Calculating statistical tests for common scenarios...");
		Map<String, DoubleStatElement> scenario_results = as.executeStatisticalTests(kept_scenarios, scenarios_v1, scenarios_v2);
		
		// Calculating degraded, optimized and unchanged scenarios
		Collection<DoubleStatElement> degraded_scenarios = AnalyzerCollectionUtil.degradedPValue(scenario_results.values(), alpha_significance_level, Tests.UTest);
		Collection<DoubleStatElement> optimized_scenarios = AnalyzerCollectionUtil.optimizedPValue(scenario_results.values(), alpha_significance_level, Tests.UTest);
		Collection<DoubleStatElement> unchanged_scenarios = AnalyzerCollectionUtil.unchangedPValue(scenario_results.values(), alpha_significance_level, Tests.UTest);
		
		// Save scenarios in separated files
		AnalyzerReportUtil.saveDoubleElements("# Scenarios executed in both releases", getFileName("kept_scenarios"), scenario_results.values(), 0, 0);
		AnalyzerReportUtil.saveSimpleElements("# Scenarios executed in the first release, but not in the second", getFileName("removed_scenarios"), AnalyzerCollectionUtil.getAll(scenarios_v1, removed_scenarios));
		AnalyzerReportUtil.saveSimpleElements("# Scenarios executed in the second release, but not in the first", getFileName("added_scenarios"), AnalyzerCollectionUtil.getAll(scenarios_v2, added_scenarios));
		
		// Save scenarios according their execution time variation measured by the UTest
		AnalyzerReportUtil.saveDoubleElements("# Degradated scenarios (UTest P-Value)", getFileName("degraded_scenarios"), degraded_scenarios, 0, alpha_significance_level);
		AnalyzerReportUtil.saveDoubleElements("# Optimized scenarios (UTest P-Value)", getFileName("optimized_scenarios"), optimized_scenarios, 0, alpha_significance_level);
		AnalyzerReportUtil.saveDoubleElements("# Unchanged scenarios (UTest P-Value)", getFileName("unchanged_scenarios"), unchanged_scenarios, 0, alpha_significance_level);
		
		System.out.println("-------------------------------------------------------------------");
		
		// Essas estruturas armazenarão os métodos comuns (com variação), adicionados e removidos de cada cenário
		Map<String, Collection<DoubleStatElement>> from_scenario_to_kept_members = new HashMap<String, Collection<DoubleStatElement>>();
		Map<String, Collection<SimpleStatElement>> from_scenario_to_added_members = new HashMap<String, Collection<SimpleStatElement>>();
		Map<String, Collection<SimpleStatElement>> from_scenario_to_removed_members = new HashMap<String, Collection<SimpleStatElement>>();
		
		int i = 0;
		
		// Cenários alvo
		Collection<DoubleStatElement> target_scenarios = new ArrayList<DoubleStatElement>();
		target_scenarios.addAll(optimized_scenarios);
		target_scenarios.addAll(degraded_scenarios);
		
		/*
		 * Esse for gera três arquivos organizados por cenários. O primeiro com todos os métodos comuns de cada cenários.
		 * O segundo com todos os métodos removidos de cada cenário. O terceiro com todos os métodos adicionados de cada cenário.
		 */
		//for (String scenario : kept_scenarios) {
		for (DoubleStatElement scenario_elem : target_scenarios) {
			String scenario = scenario_elem.getElementName();
			
			// TODO: Como otimizar a consulta do método "getSimpleStatOfMembersByScenario"?
			System.out.println(++i + " / " + target_scenarios.size() + " / " + kept_scenarios.size() + " - Getting members of the scenario " + scenario);
			
			long begin = System.currentTimeMillis();
			
			Map<String, SimpleStatElement> members_v1_by_scenario = database_v1.getSimpleStatOfMembersByScenario(scenario);
			System.out.println("\tTotal V1 = " + members_v1_by_scenario.size());
			
			Map<String, SimpleStatElement> members_v2_by_scenario = database_v2.getSimpleStatOfMembersByScenario(scenario);
			System.out.println("\tTotal V2 = " + members_v2_by_scenario.size());
			
			long end = System.currentTimeMillis();
			
			System.out.println("Both queries took " + (end - begin) / 1000 + " seg.");
			
			// Recupera as assinaturas dos métodos
			Set<String> sigs_removed_members_by_scenario = AnalyzerCollectionUtil.except(members_v1_by_scenario.keySet(), members_v2_by_scenario.keySet());
			Set<String> sigs_added_members_by_scenario = AnalyzerCollectionUtil.except(members_v2_by_scenario.keySet(), members_v1_by_scenario.keySet());
			Set<String> sigs_kept_members_by_scenario = AnalyzerCollectionUtil.intersect(members_v1_by_scenario.keySet(), members_v2_by_scenario.keySet());
			
			// Recupera os objetos a partir das assinaturas
			Collection<SimpleStatElement> removed_members_by_scenario = AnalyzerCollectionUtil.getAll(members_v1_by_scenario, sigs_removed_members_by_scenario);
			Collection<SimpleStatElement> added_members_by_scenario = AnalyzerCollectionUtil.getAll(members_v2_by_scenario, sigs_added_members_by_scenario);
			
			Map<String, DoubleStatElement> member_results_by_scenario = as.executeStatisticalTests(sigs_kept_members_by_scenario, members_v1_by_scenario, members_v2_by_scenario);
			
			from_scenario_to_kept_members.put(scenario, member_results_by_scenario.values());
			from_scenario_to_added_members.put(scenario, added_members_by_scenario);
			from_scenario_to_removed_members.put(scenario, removed_members_by_scenario);
			
			DoubleStatElement double_scenario_stat = scenario_results.get(scenario);
			
			AnalyzerReportUtil.saveDoubleElementsOfScenario("# " + i + " Common members from the execution of scenario " + scenario, getFileName("kept_members_by_scenario_with_var"), double_scenario_stat, Tests.UTest, alpha_significance_level, member_results_by_scenario.values());
			AnalyzerReportUtil.saveSimpleElementsOfScenario("# " + i + " Removed members from the execution of scenario " + scenario, getFileName("removed_members_by_scenario_with_var"), scenarios_v1.get(scenario), removed_members_by_scenario);
			AnalyzerReportUtil.saveSimpleElementsOfScenario("# " + i + " Added members to the execution of scenario " + scenario, getFileName("added_members_by_scenario_with_var"), scenarios_v2.get(scenario), added_members_by_scenario);
		}
		
		generateDAReport("deviation_members_by_degraded_scenarios", degraded_scenarios,
				from_scenario_to_kept_members, from_scenario_to_added_members, from_scenario_to_removed_members);
		
		generateDAReport("deviation_members_by_optimized_scenarios", optimized_scenarios,
				from_scenario_to_kept_members, from_scenario_to_added_members, from_scenario_to_removed_members);
		/*
		// Só faz sentido esse relatório se rodar o FOR acima para todos os cenários mantidos
		generateDAReport("deviation_members_by_unchanged_scenarios", unchanged_scenarios,
				from_scenario_to_kept_members, from_scenario_to_added_members, from_scenario_to_removed_members);
		*/
		
		System.out.println("-------------------------------------------------------------------");
		
		return strdate;
	}

	private void generateDAReport(String filename, Collection<DoubleStatElement> target_scenarios,
			Map<String, Collection<DoubleStatElement>> from_scenario_to_kept_members,
			Map<String, Collection<SimpleStatElement>> from_scenario_to_added_members,
			Map<String, Collection<SimpleStatElement>> from_scenario_to_removed_members) throws FileNotFoundException {
		int i = 0;
		
		/*
		 * Esse for gera três arquivos organizados por cenários. O primeiro com todos os métodos comuns (e que possuem variação) de cada cenários.
		 * O segundo com todos os métodos removidos de cada cenário. O terceiro com todos os métodos adicionados de cada cenário.
		 */
		for (DoubleStatElement sdegraded: target_scenarios) {
			System.out.println(++i + " / " + target_scenarios.size() + " - Filtrating members with deviation for degraded scenario " + sdegraded.getElementName());
			
			String scenario = sdegraded.getElementName();
			
			Collection<DoubleStatElement> deviation_members_of_scenario = AnalyzerCollectionUtil.filterDoubleElementByDeviation(
					from_scenario_to_kept_members.get(sdegraded.getElementName()),
					Arrays.asList(new Deviation[]{Deviation.OPTIMIZATION, Deviation.DEGRADATION}),
					Tests.UTest, alpha_significance_level);
			
			Collection<SimpleStatElement> significant_added_members_of_scenario = AnalyzerCollectionUtil.filterSimpleElementByTimeLimite(
					from_scenario_to_added_members.get(sdegraded.getElementName()), 0);
			
			Collection<SimpleStatElement> significant_removed_members_of_scenario = AnalyzerCollectionUtil.filterSimpleElementByTimeLimite(
					from_scenario_to_removed_members.get(sdegraded.getElementName()), 0);
			
			AnalyzerReportUtil.saveDoubleElementsOfScenario("# " + i + " Members with deviation for scenario " + scenario, getFileName(filename), sdegraded, Tests.UTest, alpha_significance_level, deviation_members_of_scenario);
			AnalyzerReportUtil.saveSimpleElements("# " + i + " Added members to the execution of scenario " + scenario, getFileName(filename), significant_added_members_of_scenario);
			AnalyzerReportUtil.saveSimpleElements("# " + i + " Removed members from the execution of scenario " + scenario, getFileName(filename), significant_removed_members_of_scenario);
		}
	}

}
