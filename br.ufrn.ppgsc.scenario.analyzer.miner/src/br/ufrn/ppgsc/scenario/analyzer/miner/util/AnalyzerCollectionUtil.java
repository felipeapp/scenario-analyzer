package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.miner.model.Commit;
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
					for (Issue issue : line.getCommit().getIssues())
						issue_numbers.add(issue.getNumber());

		return issue_numbers;
	}
	
	public static String getLineOfIssues(Commit commit, char separator) {
		StringBuilder sb = new StringBuilder();
		
		for (Issue issue : commit.getIssues()) {
			sb.append(issue.getNumber());
			sb.append(separator);
		}
		
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);
		
		return sb.toString();
	}
	
	public static Collection<Commit> getCommitsFromUpMethods(Collection<UpdatedMethod> up_methods) {
		Collection<Commit> commits = new HashSet<Commit>();

		for (UpdatedMethod method : up_methods)
			for (UpdatedLine line : method.getUpdatedLines())
				commits.add(line.getCommit());

		return commits;
	}
	
	public static Collection<String> getCommitProperties(Collection<Commit> commits, String separator) {
		Collection<String> result = new ArrayList<String>();
		
		// Building the header
		StringBuilder sb = new StringBuilder();
		
		sb.append("Revision" + separator);
		sb.append("Bug Fixing" + separator);
		sb.append("Author" + separator);
		sb.append("Date" + separator);
		sb.append("Number of Packages" + separator);
		sb.append("Number of Files" + separator);
		sb.append("Number of Insertions" + separator);
		sb.append("Number of Deletions" + separator);
		sb.append("Number of Churns" + separator);
		sb.append("Delta of Lines" + separator);
		sb.append("Number of Hunks" + separator);
		sb.append("Number of Issues" + separator);
		sb.append("Issues");
		
		result.add(sb.toString());
		
		// Getting properties
		for (Commit commit : commits)
			result.add(getCommitProperties(commit, separator));
		
		return result;
	}
	
	public static String getCommitProperties(Commit commit, String separator) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(commit.getRevision());
		sb.append(separator);
		sb.append(commit.isBugFixing());
		sb.append(separator);
		sb.append(commit.getAuthor());
		sb.append(separator);
		sb.append(commit.getFormatedDate());
		sb.append(separator);
		sb.append(commit.getPackages().size());
		sb.append(separator);
		sb.append(commit.getStats().size());
		sb.append(separator);
		sb.append(commit.getNumberOfInsertions());
		sb.append(separator);
		sb.append(commit.getNumberOfDeletions());
		sb.append(separator);
		sb.append(commit.getNumberOfChurns());
		sb.append(separator);
		sb.append(commit.getDeltaOfLines());
		sb.append(separator);
		sb.append(commit.getNumberOfHunks());
		sb.append(separator);
		sb.append(commit.getIssues().size());
		sb.append(separator);
		sb.append(getLineOfIssues(commit, ':'));
		
		return sb.toString();
	}
	
	public static String getCommitSelectedProperties(Commit commit, String separator) {
		StringBuilder sb = new StringBuilder();
		
		Calendar c = new GregorianCalendar();
		c.setTime(commit.getDate());
		
		sb.append(commit.getRevision());
		sb.append(separator);
		sb.append(commit.isBugFixing());
		sb.append(separator);
		sb.append(commit.getPackages().size());
		sb.append(separator);
		sb.append(commit.getStats().size());
		sb.append(separator);
		sb.append(commit.getIssues().size());
		sb.append(separator);
		sb.append(commit.getNumberOfInsertions());
		sb.append(separator);
		sb.append(commit.getNumberOfDeletions());
		sb.append(separator);
		sb.append(commit.getNumberOfChurns());
		sb.append(separator);
		sb.append(commit.getDeltaOfLines());
		sb.append(separator);
		sb.append(commit.getNumberOfHunks());
		sb.append(separator);
		sb.append(commit.getDateProperty(Calendar.HOUR_OF_DAY));
		sb.append(separator);
		sb.append(commit.getDateProperty(Calendar.DAY_OF_WEEK));
		
		return sb.toString();
	}
	
	public static Map<String, Integer> countTaskTypes(Map<String, Collection<UpdatedMethod>> map_path_methods) {
		Map<String, Integer> counter_task_types = new HashMap<String, Integer>();
		Set<Long> counted_tasks = new HashSet<Long>();

		for (String path : map_path_methods.keySet()) {

			for (UpdatedMethod method : map_path_methods.get(path)) {

				for (UpdatedLine line : method.getUpdatedLines()) {

					for (Issue issue : line.getCommit().getIssues()) {

						if (issue.getId() > 0 && !counted_tasks.contains(issue.getId())) {
							Integer counter = counter_task_types.get(issue.getType());

							if (counter == null)
								counter_task_types.put(issue.getType(), 1);
							else
								counter_task_types.put(issue.getType(), counter + 1);
							
							counted_tasks.add(issue.getId());
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

					for (Issue issue : line.getCommit().getIssues()) {

						if (issue.getId() > 0 && !(counted_tasks.contains(issue.getId()) && counted_members.contains(method.getMethodLimit().getSignature()))) {
							Collection<UpdatedMethod> list = task_members.get(issue.getType());

							if (list == null) {
								list = new ArrayList<UpdatedMethod>();
								list.add(method);
								task_members.put(issue.getType(), list);
							}
							else {
								task_members.get(issue.getType()).add(method);
							}
							
							counted_tasks.add(issue.getId());
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
	
	public static boolean matchesExcludeWord(String text) {
		String exclude_word = SystemMetadataUtil.getInstance().getStringProperty("exclude_word");
		return (exclude_word != null && !exclude_word.isEmpty() && text.contains(exclude_word));
	}
	
}
