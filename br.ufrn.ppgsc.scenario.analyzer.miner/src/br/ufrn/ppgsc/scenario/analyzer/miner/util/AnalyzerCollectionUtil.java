package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.StatElement;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;

public abstract class AnalyzerCollectionUtil {

	public static <T> Set<T> except(Collection<T> setA, Collection<T> setB) {
		Set<T> except = new HashSet<T>(setA);
		except.removeAll(setB);
		return except;
	}

	public static <T> Set<T> intersect(Collection<T> setA, Collection<T> setB) {
		Set<T> intersect = new HashSet<T>(setA);
		intersect.retainAll(setB);
		return intersect;
	}

	/** It uses the rate method to determine if there was performance degradation */
	public static List<StatElement> degradatedRate(Collection<StatElement> elements, double rate) {
		List<StatElement> result = new ArrayList<StatElement>();

		for (StatElement e : elements)
			if (e.getAVGv2() > e.getAVGv1() * (1 + rate))
				result.add(e);

		return result;
	}

	/** It uses the rate method to determine if there was performance optimization */
	public static List<StatElement> optimizedRate(Collection<StatElement> elements, double rate) {
		List<StatElement> result = new ArrayList<StatElement>();

		for (StatElement e : elements)
			if (e.getAVGv2() < e.getAVGv1() * (1 - rate))
				result.add(e);

		return result;
	}

	/** It uses the rate method to determine unchanged performance */
	public static List<StatElement> unchangedRate(Collection<StatElement> elements, double rate) {
		List<StatElement> result = new ArrayList<StatElement>();

		for (StatElement e : elements)
			if (e.getAVGv2() >= e.getAVGv1() * (1 - rate) && e.getAVGv2() <= e.getAVGv1() * (1 + rate))
				result.add(e);

		return result;
	}

	/** It uses the pvalue to determine performance degradation */
	public static List<StatElement> degradatedPValue(Collection<StatElement> elements, double alpha, AnalyzerStatistical.Tests test) {
		List<StatElement> result = new ArrayList<StatElement>();

		for (StatElement e : elements) {
			double pvalue;
			
			if (test == AnalyzerStatistical.Tests.TTest)
				pvalue = e.getTTestPvalue();
			else if (test == AnalyzerStatistical.Tests.UTest)
				pvalue = e.getUTestPvalue();
			else
				throw new RuntimeException("Target test invalid at runtime");
			
			if (pvalue <= alpha && e.getAVGv2() - e.getAVGv1() > 0)
				result.add(e);
		}

		return result;
	}
	
	/** It uses the pvalue to determine if there was performance optimization */
	public static List<StatElement> optimizedPValue(Collection<StatElement> elements, double alpha, AnalyzerStatistical.Tests test) {
		List<StatElement> result = new ArrayList<StatElement>();

		for (StatElement e : elements) {
			double pvalue;
			
			if (test == AnalyzerStatistical.Tests.TTest)
				pvalue = e.getTTestPvalue();
			else if (test == AnalyzerStatistical.Tests.UTest)
				pvalue = e.getUTestPvalue();
			else
				throw new RuntimeException("Target test invalid at runtime");
			
			if (pvalue <= alpha && e.getAVGv2() - e.getAVGv1() < 0)
				result.add(e);
		}

		return result;
	}
	
	/** It uses the pvalue to determine unchanged performance */
	public static List<StatElement> unchangedPValue(Collection<StatElement> elements, double alpha, AnalyzerStatistical.Tests test) {
		List<StatElement> result = new ArrayList<StatElement>();

		for (StatElement e : elements) {
			double pvalue;
			
			if (test == AnalyzerStatistical.Tests.TTest)
				pvalue = e.getTTestPvalue();
			else if (test == AnalyzerStatistical.Tests.UTest)
				pvalue = e.getUTestPvalue();
			else
				throw new RuntimeException("Target test invalid at runtime");
			
			if (pvalue > alpha || Double.isNaN(pvalue))
				result.add(e);
		}

		return result;
	}
	
	public static Set<Long> getTaskNumbers(Map<String, Collection<UpdatedMethod>> map_path_methods) {
		Set<Long> issue_numbers = new HashSet<Long>();

		for (String path : map_path_methods.keySet())
			for (UpdatedMethod method : map_path_methods.get(path))
				for (UpdatedLine line : method.getUpdatedLines())
					for (Issue issue : line.getIssues())
						issue_numbers.add(issue.getNumber());

		return issue_numbers;
	}
	
	public static Map<String, Integer> countTaskTypes(Map<String, Collection<UpdatedMethod>> map_path_methods) {
		Map<String, Integer> counter_task_types = new HashMap<String, Integer>();
		Set<Long> counted_tasks = new HashSet<Long>();

		for (String path : map_path_methods.keySet()) {

			for (UpdatedMethod method : map_path_methods.get(path)) {

				for (UpdatedLine line : method.getUpdatedLines()) {

					for (Issue issue : line.getIssues()) {

						if (issue.getIssueId() > 0 && !counted_tasks.contains(issue.getIssueId())) {
							Integer counter = counter_task_types.get(issue.getIssueType());

							if (counter == null)
								counter_task_types.put(issue.getIssueType(), 1);
							else
								counter_task_types.put(issue.getIssueType(), counter + 1);
							
							counted_tasks.add(issue.getIssueId());
						}

					}

				}

			}
		}

		return counter_task_types;
	}
	
	public static Map<String, Collection<UpdatedMethod>> getTaskMembers(Map<String, Collection<UpdatedMethod>> map_path_methods) {
		Map<String, Collection<UpdatedMethod>> task_members = new HashMap<String, Collection<UpdatedMethod>>();
		
		Set<Long> counted_tasks = new HashSet<Long>();
		Set<String> counted_members = new HashSet<String>();

		for (String path : map_path_methods.keySet()) {

			for (UpdatedMethod method : map_path_methods.get(path)) {

				for (UpdatedLine line : method.getUpdatedLines()) {

					for (Issue issue : line.getIssues()) {

						if (issue.getIssueId() > 0 && !(counted_tasks.contains(issue.getIssueId()) && counted_members.contains(method.getMethodLimit().getSignature()))) {
							Collection<UpdatedMethod> list = task_members.get(issue.getIssueType());

							if (list == null) {
								list = new ArrayList<UpdatedMethod>();
								list.add(method);
								task_members.put(issue.getIssueType(), list);
							}
							else {
								task_members.get(issue.getIssueType()).add(method);
							}
							
							counted_tasks.add(issue.getIssueId());
							counted_members.add(method.getMethodLimit().getSignature());
						}

					}

				}

			}
		}

		return task_members;
	}

	public static Map<String, List<String>> getScenariosWithBlames(
			String degraded_scenarios_filename, String degraded_methods_filename) throws IOException {
		
		Map<String, List<String>> scenario_to_blame = new HashMap<String, List<String>>();
		Collection<String> degraded_scenarios = new HashSet<String>();
		
		AnalyzerReportUtil.loadCollection(degraded_scenarios, degraded_scenarios_filename);
		BufferedReader br = new BufferedReader(new FileReader(degraded_methods_filename));
		
		for (String sname : degraded_scenarios)
			scenario_to_blame.put(sname, new ArrayList<String>());
		
		System.out.println("Processing >> " + br.readLine());
		
		int number_of_methods = Integer.parseInt(br.readLine());
		
		for (int i = 0; i < number_of_methods; i++) {
			String signature = br.readLine();
			int number_of_scenarios = Integer.parseInt(br.readLine());
			
			for (int j = 0; j < number_of_scenarios; j++) {
				String scenario_name = br.readLine();
				Collection<String> blame_signature = scenario_to_blame.get(scenario_name);
				
				if (blame_signature != null)
					blame_signature.add(signature);
			}
		}

		br.close();
		
		return scenario_to_blame;
	}
	
}
