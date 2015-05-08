package br.ufrn.ppgsc.scenario.analyzer.miner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import br.ufrn.ppgsc.scenario.analyzer.miner.db.DatabaseRelease;
import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IPathTransformer;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerCollectionUtil;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerReportUtil;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.RepositoryManager;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public final class AnalyzerMinerRepositoryRunnable {

	private List<String> active_targets;
	private IPathTransformer transformer;
	
	private String repository_url;
	private String repository_prefix;
	private String repository_user;
	private String repository_password;
	private String system_id;
	private String strdate;
	private String workcopy_prefix_r1;
	private String workcopy_prefix_r2;
	private String exclude_word;
	
	private String[] comparison_strategy;
	private String[] exclude_entry_points;
	
	private double avg_significance_delta;
	private double member_significance_variation;
	private int package_deep;

	private static final String[] TARGET_FILES = {
		// scenarios performance
		"kept_scenarios",
		"removed_scenarios",
		"added_scenarios",
		"pr_degraded_scenarios",
		"pr_optimized_scenarios",
		"pr_unchanged_scenarios",
		"pt_degraded_scenarios",
		"pt_optimized_scenarios",
		"pt_unchanged_scenarios",
		"pu_degraded_scenarios",
		"pu_optimized_scenarios",
		"pu_unchanged_scenarios",
		// methods performance
		"kept_methods",
		"removed_methods",
		"added_methods",
		"pr_degraded_methods",
		"pr_optimized_methods",
		"pr_unchanged_methods",
		"pt_degraded_methods",
		"pt_optimized_methods",
		"pt_unchanged_methods",
		"pu_degraded_methods",
		"pu_optimized_methods",
		"pu_unchanged_methods",
		// scenarios and methods failed
		"failed_scenarios_r1",
		"failed_scenarios_r2",
		"failed_methods_only_r1",
		"failed_methods_only_r2",
		"failed_methods_both"
	};

	public AnalyzerMinerRepositoryRunnable(String strdate) throws FileNotFoundException {
		SystemMetadataUtil properties = SystemMetadataUtil.getInstance();
		
		transformer = properties.newObjectFromProperties(IPathTransformer.class);
		this.strdate = strdate;
		active_targets = new ArrayList<String>();
		
		repository_url = properties.getStringProperty("repository_url");
		repository_prefix = properties.getStringProperty("repository_prefix");
		repository_user = properties.getStringProperty("repository_user");
		repository_password = properties.getStringProperty("repository_password");
		system_id = properties.getStringProperty("system_id");
		
		workcopy_prefix_r1 = properties.getStringProperty("workcopy_prefix_r1");
		workcopy_prefix_r2 = properties.getStringProperty("workcopy_prefix_r2");
		
		exclude_word = properties.getStringProperty("exclude_word");
		
		package_deep = properties.getIntProperty("package_deep");
		avg_significance_delta = properties.getDoubleProperty("avg_significance_delta");
		member_significance_variation = properties.getDoubleProperty("member_significance_variation");
		
		comparison_strategy = properties.getStringProperty("comparison_strategy").split(";");
		exclude_entry_points = properties.getStringProperty("exclude_entry_points").split(";");
		
		for (String name : TARGET_FILES)
			if (properties.getBooleanProperty(name))
				active_targets.add(name);
		
	}
	
	/*
	 * TODO: How can I move this to report util?
	 * It returns the set of commits significantly blamed for performance variation
	 */
	private Set<String> saveScenariosAndBlames(String message, String filename, Map<String, List<String>> scenario_to_blames,
			Map<String, Double> avg_time_members_v1, Map<String, Double> avg_time_members_v2,
			Map<String, UpdatedMethod> map_signature_to_upmethod) throws FileNotFoundException {
		
		System.out.println("Saving >> " + message);
		
		int total_members = 0;
		int total_scenarios_with_blames = 0;
		int total_members_without_word = 0;
		
		Set<String> members_signature = new TreeSet<String>();
		Set<String> members_with_information = new TreeSet<String>();
		Set<String> counted = new HashSet<String>();
		
		Map<Long, Issue> number_to_issue = new HashMap<Long, Issue>();
		Map<String, Set<Issue>> revision_to_issues = new HashMap<String, Set<Issue>>();
		
		Map<String, Set<String>> scenario_to_members_significance = new HashMap<String, Set<String>>();
		Map<Long, Issue> number_to_issue_significance = new HashMap<Long, Issue>();
		Map<String, Set<Issue>> revision_to_issues_significance = new HashMap<String, Set<Issue>>();
		
		PrintWriter pw = new PrintWriter(getRMFilePath(filename));
		PrintWriter pw_list = new PrintWriter(getRMFilePath(filename + "_summary"));
		PrintWriter pw_significance = new PrintWriter(getRMFilePath(filename + "_significance"));
		
		// We need the loop to count because some of them can be empty
		for (List<String> list : scenario_to_blames.values())
			if (!list.isEmpty())
				++total_scenarios_with_blames;
		
		pw.println(message);
		pw_list.println(message + " [summary]");
		pw_significance.println(message + " [significance]");
		
		// Number of scenario that have blamed members
		pw.println("Number of scenarios: " + total_scenarios_with_blames);
		pw_list.println("Number of scenarios: " + total_scenarios_with_blames);
		
		/* 
		 * Some of the scenarios might not have blame members.
		 * It happens because the sum of small variation of members 
		 * that are not covered by the comparison methods, but when they are put
		 * together, they are bigger enough to impact the scenario
		 */
		pw.println("Number of scenarios (include empties): " + scenario_to_blames.size());
		
		int cscenarios = 0;
		for (String scenario : new TreeSet<String>(scenario_to_blames.keySet())) {
			++cscenarios;
			
			List<String> signatures = scenario_to_blames.get(scenario);
			
			Collections.sort(signatures);
			
			pw.println(scenario);
			
			int i = 0;
			for (String s : signatures)
				if (!matchesExcludeWord(s))
					++i;
			
			pw.println("\tNumber of methods: " + signatures.size());
			pw.println("\tNumber of methods (exclude word applied): " + i);
			
			if (i > 0) {
				pw_list.println(scenario);
				// We will suppose it is significant
				scenario_to_members_significance.put(scenario, new HashSet<String>());
			}
			
			int csignatures = 0;
			for (String s : signatures) {
				++csignatures;
				
				Map<Long, Issue> local_number_to_issue = new HashMap<Long, Issue>();
				Map<String, Set<Issue>> local_revision_to_issues = new HashMap<String, Set<Issue>>();
				
				Double t1 = avg_time_members_v1.get(s);
				Double t2 = avg_time_members_v2.get(s);
				
				double delta = calcExecutionTimeAbsDelta(t1, t2);
				
				System.out.println(cscenarios + "/" + scenario_to_blames.size() + "-" + csignatures + "/" + signatures.size() + " | Scenarios and blames: Counting " + s + " in R1 for " + scenario);
				int count1 = DatabaseRelease.getDatabasev1().countMethodExecutionByScenario(scenario, s);
				System.out.println("\tTotal = " + count1);
				
				System.out.println(cscenarios + "/" + scenario_to_blames.size() + "-" + csignatures + "/" + signatures.size() + " | Scenarios and blames: Counting " + s + " in R2 for " + scenario);
				int count2 = DatabaseRelease.getDatabasev2().countMethodExecutionByScenario(scenario, s);
				System.out.println("\tTotal = " + count2);
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("\t" + s + System.lineSeparator());
				sb.append("\t\tTime: " + t1 + ";" + t2 + ";" + delta + ";" + count1 + ";" + count2 + ";" + System.lineSeparator());
				
				UpdatedMethod um = map_signature_to_upmethod.get(s);
				for (UpdatedLine ul : um.getUpdatedLines()) {
					Set<Issue> issues_from_map = local_revision_to_issues.get(ul.getCommit().getRevision());
					
					for (Issue issue  : ul.getCommit().getIssues())
						local_number_to_issue.put(issue.getNumber(), issue);
					
					if (issues_from_map == null) {
						issues_from_map = new HashSet<Issue>();
						local_revision_to_issues.put(ul.getCommit().getRevision(), issues_from_map);
					}
					
					issues_from_map.addAll(ul.getCommit().getIssues());
				}
				
				for (String revision : local_revision_to_issues.keySet()) {
					sb.append("\t\t\tRevision: " + revision + System.lineSeparator());
					
					sb.append("\t\t\t\tIssues: ");
					for (Issue issue : local_revision_to_issues.get(revision))
						sb.append(issue.getNumber() + ";");
					sb.deleteCharAt(sb.length() - 1);
					sb.append(System.lineSeparator());
				}
				
//				text += System.lineSeparator() + "\t\t";
//				for (Issue issue : local_number_to_issue.values())
//					text += ";" + issue.getNumber();
				
				pw.print(sb.toString());
				// It will count including the tests
				//members_with_time.add(text);
				//number_to_issue.putAll(local_number_to_issue);
				
				if (!counted.contains(s)) {
					counted.add(s);
					++total_members;
					
					if (!matchesExcludeWord(s) && delta >= avg_significance_delta) {
						String aux = s + ";" + t1 + ";" + t2 + ";" + delta + ";" + count1 + ";" + count2;
						
						++total_members_without_word;
						members_with_information.add(sb.toString());
						members_signature.add(aux);
						number_to_issue.putAll(local_number_to_issue);
						revision_to_issues.putAll(local_revision_to_issues);
					}
				}
				
				// If it is significant inside the scenario we save in another file
				if (!matchesExcludeWord(s) && isExecutionTimeSignificant(count1, count2, t1, t2)) {
					String aux = s + ";" + t1 + ";" + t2 + ";" + delta + ";" + count1 + ";" + count2;
					
					scenario_to_members_significance.get(scenario).add(aux);
					number_to_issue_significance.putAll(local_number_to_issue);
					revision_to_issues_significance.putAll(local_revision_to_issues);
				}
			}
		}
		
		pw.println(total_members);
		pw.println(total_members_without_word);
		
		StringBuilder sb_scenarios = new StringBuilder();
		StringBuilder sb_members = new StringBuilder();
		Set<String> counted_member = new HashSet<String>();
		int count_scenarios = 0;
		int count_members = 0;
		for (String scenario : scenario_to_members_significance.keySet()) {
			Set<String> members = scenario_to_members_significance.get(scenario);
			
			if (!members.isEmpty()) {
				sb_scenarios.append(scenario + System.lineSeparator());
				count_scenarios++;
				
				for (String line : members) {
					String sig = line.substring(0, line.indexOf(';'));
					
					if (!counted_member.contains(sig)) {
						sb_members.append(line + System.lineSeparator());
						count_members++;
						counted_member.add(sig);
					}
				}
			}
		}
		sb_scenarios.insert(0, count_scenarios + System.lineSeparator());
		sb_members.insert(0, count_members + System.lineSeparator());
		pw_significance.print(sb_scenarios);
		pw_significance.print(sb_members);
		
		// After that, print the association
		StringBuilder sb_lines = new StringBuilder();
		int nb_lines = 0;
		for (String scenario : scenario_to_members_significance.keySet()) {
			for (String member : scenario_to_members_significance.get(scenario)) {
				sb_lines.append(scenario + ";" + member + System.lineSeparator());
				nb_lines++;
			}
		}
		sb_lines.insert(0, nb_lines + System.lineSeparator());
		pw_significance.print(sb_lines.toString());
		
		pw_list.println(members_signature.size());
		for (String member : members_signature)
			pw_list.println(member);
		
		pw_list.println(members_with_information.size());
		for (String member : members_with_information)
			pw_list.print(member);
		
		printIssues(number_to_issue, revision_to_issues, pw_list);
		printIssues(number_to_issue_significance, revision_to_issues_significance, pw_significance);
		
		AnalyzerReportUtil.saveCollection(message, getRMFilePath(filename + "_significance_only_commits"), revision_to_issues_significance.keySet());
		
		pw.close();
		pw_list.close();
		pw_significance.close();
		
		return revision_to_issues_significance.keySet();
	}
	
	// TODO: Melhorar este método depois
	private void printIssues(Map<Long, Issue> number_to_issue, Map<String, Set<Issue>> revision_to_issues, PrintWriter pw) {
		pw.println(number_to_issue.size());
		for (long issue_number : new TreeSet<Long>(number_to_issue.keySet()))
			pw.println(issue_number + ";" + number_to_issue.get(issue_number).getType());
		
		Map<String, Set<Long>> issuetype_to_number = new HashMap<String, Set<Long>>();
		for (Issue issue  : number_to_issue.values()) {
			Set<Long> issue_number_list = issuetype_to_number.get(issue.getType());
			
			if (issue_number_list == null) {
				issue_number_list = new TreeSet<Long>();
				issuetype_to_number.put(issue.getType(), issue_number_list);
			}
			
			issue_number_list.add(issue.getNumber());
		}
		
		pw.println(issuetype_to_number.size());
		for (String issue_type : issuetype_to_number.keySet())
			pw.println(issue_type + ";" + issuetype_to_number.get(issue_type).size());
		
		pw.println(issuetype_to_number.size());
		for (String issue_type : issuetype_to_number.keySet()) {
			pw.print(issue_type);
			for (long numbers : issuetype_to_number.get(issue_type))
				pw.print(";" + numbers);
			pw.println();
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("Number of revisions (commits): " + revision_to_issues.size() + System.lineSeparator());
		for (String revision : revision_to_issues.keySet()) {
			sb.append("Revision (Issues): " + revision + "(");
			for (Issue issue : revision_to_issues.get(revision))
				sb.append(issue.getNumber() + ";");
			sb.deleteCharAt(sb.length() - 1);
			sb.append(")" + System.lineSeparator());
		}
		pw.print(sb.toString());
	}
	
	// TODO: How can I move this to report util?
	private String getDAFilePath(String partial_name) {
		return "reports/degradation_analysis/" + system_id + "_" + partial_name + "_" + strdate + ".txt";
	}
	
	// TODO: How can I move this to report util?
	private String getRMFilePath(String partial_name) {
		return "reports/repository_mining/" + system_id + "_" + partial_name + "_" + strdate + ".txt";
	}
	
	private boolean isFilled(Map<?, ?> m) {
		return m != null && !m.isEmpty();
	}
	
	public void run() throws IOException {
		// It connects to the repository at the time it is created
		RepositoryManager repository = new RepositoryManager(repository_url, repository_user, repository_password);
		
		// Methods that have contributed to performance degradation. The key is the method signature
		Map<String, Map<String, UpdatedMethod>> map_of_p_degradation_methods = new HashMap<String, Map<String, UpdatedMethod>>();
		
		// Methods that have contributed to performance optimization. The key is the method signature
		Map<String, Map<String, UpdatedMethod>> map_of_p_optimization_methods = new HashMap<String, Map<String, UpdatedMethod>>();
		
		for (String filename : active_targets) {
			List<String> repository_paths = new ArrayList<String>();
			List<String> old_workcopy_paths = new ArrayList<String>();
			List<String> new_workcopy_paths = new ArrayList<String>();
			
			/*
			 * It will be methods and their statistical. The keys are their signatures.
			 * We will have to filter the unchanged methods of this collection.
			 */
			Collection<String> full_signatures = new ArrayList<String>();
			
			String file_message = AnalyzerReportUtil.loadCollection(full_signatures, getDAFilePath(filename));
			if (filename.endsWith("degraded_methods"))
				AnalyzerReportUtil.loadCollection(full_signatures, getDAFilePath("added_methods"));
			else if (filename.endsWith("optimized_methods"))
				AnalyzerReportUtil.loadCollection(full_signatures, getDAFilePath("removed_methods"));
			
			for (String signature : full_signatures) {
				String paths[] = transformer.convert(signature, repository_prefix, workcopy_prefix_r1, workcopy_prefix_r2);
				
				if (paths == null) {
					System.out.println("Ignoring " + signature + " because paths is null (Is it right?).");
					continue;
				}
				
				boolean ignore_file = false;
				boolean file_exists_r1 = new File(paths[1]).exists();
				boolean file_exists_r2 = new File(paths[2]).exists();
				
				if (!file_exists_r1 && !file_exists_r2) {
					throw new FileNotFoundException("\n" + paths[1] + "\n" + paths[2]);
					//System.err.println("\n" + paths[1] + "\n" + paths[2]);
				}
				
				if (!file_exists_r1) {
					System.out.println("Not found in R1 (Is it right?): " + paths[1]);
					ignore_file = true;
				}
				
				if (!file_exists_r2) {
					System.out.println("Not found in R2 (Is it right?): " + paths[2]);
					ignore_file = true;
				}
				
				if (!ignore_file) {
					repository_paths.add(paths[0]);
					old_workcopy_paths.add(paths[1]);
					new_workcopy_paths.add(paths[2]);
				}
			}
			
			System.out.println("Getting updated methods from repository to " +
					filename + " [" 	+ repository_paths.size() + ", " + file_message + "]");
			
			/*
			 * This map will save all the changes for each method declaration inside
			 * the classes of the methods that the degradation analysis found.
			 * After that, we will have to filter the methods that do not are our
			 * interest. Is is solved checking if the method is inside the list of interest.
			 * The keys are the paths to classes and the values are the updated methods of
			 * all methods of the class related with the path.
			 */
			Map<String, Collection<UpdatedMethod>> full_path_to_upmethod = null;
			
			if (!full_signatures.isEmpty())
				full_path_to_upmethod = repository.getUpdatedMethodsFromRepository(repository_paths, old_workcopy_paths, new_workcopy_paths);
			
			if (full_path_to_upmethod == null)
				full_path_to_upmethod = new HashMap<String, Collection<UpdatedMethod>>();
			
			// This contains the methods of interest. The keys are the paths to classes
			Map<String, Collection<UpdatedMethod>> filtered_path_to_upmethod = new HashMap<String, Collection<UpdatedMethod>>();
			
			// Check every path to filter the collection of methods
			for (String path : full_path_to_upmethod.keySet()) {
				// Check every method of the path
				for (UpdatedMethod upm : full_path_to_upmethod.get(path)) {
					/* 
					 * Check if the method name matches with the method signature.
					 * There is a limitation when the method has the same name with
					 * different parameters. This is caused because of the JDT parser.
					 * TODO: Resolve the JDT parser problem to recovery the full name of
					 * methods, including their parameters.
					 * 
					 */
					Collection<String> matched_signatures = matchesName(path, upm.getMethodLimit().getSignature(), full_signatures);
					
					if (!matched_signatures.isEmpty()) {
						Collection<UpdatedMethod> collection = filtered_path_to_upmethod.get(path);
						
						if (collection == null) {
							collection = new ArrayList<UpdatedMethod>();
							filtered_path_to_upmethod.put(path, collection);
						}
						
						collection.add(upm);
						
						/*
						 * Comments about the two IFs below.
						 * 
						 * When analyzing methods, it saves them for posterior analysis.
						 * Do not forget that you cannot trust totally in the matched_signature.
						 * 
						 * TODO
						 * Think about that: added methods can contribute with
						 * performance degradation and the test is below. Removed methods can
						 * contribute with performance optimization, but I will not find them
						 * because their code is not in the final release anymore. I still
						 * put the test, but I think it does not make difference.
						 */
						Map<String, UpdatedMethod> current_sig_to_upm = new HashMap<String, UpdatedMethod>();
						
						for (String s : matched_signatures)
							current_sig_to_upm.put(s, upm);
						
						
						for (String prefix : comparison_strategy) {
							if (filename.equals(prefix + "degraded_methods")) {
								Map<String, UpdatedMethod> current_stored = map_of_p_degradation_methods.get(prefix);
								
								if (current_stored == null)
									map_of_p_degradation_methods.put(prefix, current_sig_to_upm);
								else
									current_stored.putAll(current_sig_to_upm);
							}
							else if (filename.equals(prefix + "optimized_methods")) {
								Map<String, UpdatedMethod> current_stored = map_of_p_optimization_methods.get(prefix);
								
								if (current_stored == null)
									map_of_p_optimization_methods.put(prefix, current_sig_to_upm);
								else
									current_stored.putAll(current_sig_to_upm);
							}
						}
					}
				}
			}
						
			// Collect some issue information for the full list of changed methods
			Map<String, Collection<UpdatedMethod>> full_issuetype_to_upmethod = AnalyzerCollectionUtil.getTaskMembers(full_path_to_upmethod);
			Map<String, Integer> full_issuetype_to_count = AnalyzerCollectionUtil.countTaskTypes(full_path_to_upmethod);
			Collection<Long> full_issue_numbers = AnalyzerCollectionUtil.getTaskNumbers(full_path_to_upmethod);
			
			// Collect some issue information for the filtered list of changed methods
			Map<String, Collection<UpdatedMethod>> filtered_issuetype_to_upmethod = AnalyzerCollectionUtil.getTaskMembers(filtered_path_to_upmethod);
			Map<String, Integer> filtered_issuetype_to_count = AnalyzerCollectionUtil.countTaskTypes(filtered_path_to_upmethod);
			Collection<Long> filtered_issue_numbers = AnalyzerCollectionUtil.getTaskNumbers(filtered_path_to_upmethod);
			
			// Save the results of all methods from degradation analysis
			AnalyzerReportUtil.saveFullMiningData(file_message + " (after mining phase)", getRMFilePath("full_" + filename), full_path_to_upmethod,
					full_issue_numbers,	full_issuetype_to_count, full_issuetype_to_upmethod);
			
			// Save the results of the filtered methods (degraded and changed)
			AnalyzerReportUtil.saveFullMiningData(file_message + " (before mining phase)", getRMFilePath("filtrated_" + filename), filtered_path_to_upmethod,
					filtered_issue_numbers,	filtered_issuetype_to_count, filtered_issuetype_to_upmethod);
		}
		
		// Save the list of commits found during the mining phase
		AnalyzerReportUtil.saveCollection("# List of all commits from classes found during the repository mining phase (blamed or not)",
				getRMFilePath("all_commits_classes"), AnalyzerCollectionUtil.getCommitProperties(repository.getAllCommits()));
		AnalyzerReportUtil.saveCollection("# List of all commits from changed methods found during the repository mining phase (blamed or not)",
				getRMFilePath("all_commits_changed_methods"), AnalyzerCollectionUtil.getCommitProperties(repository.getCommitsFromChangedMethods()));
		
//		System.out.println("Getting methods and means for release 1...");
//		Map<String, Double> avg_time_members_v1 = DatabaseRelease.getDatabasev1().getExecutionTimeAverageOfMembers();
//		System.out.println("\tTotal = " + avg_time_members_v1.size());
//		
//		System.out.println("Getting methods and means for release 2...");
//		Map<String, Double> avg_time_members_v2 = DatabaseRelease.getDatabasev2().getExecutionTimeAverageOfMembers();
//		System.out.println("\tTotal = " + avg_time_members_v1.size());
		
		Map<String, Double> avg_time_members_v1 = new HashMap<String, Double>();
		Map<String, Double> avg_time_members_v2 = new HashMap<String, Double>();
		
		System.out.println("Getting methods and means for target releases...");
		AnalyzerReportUtil.loadTimeOfKeptElements(avg_time_members_v1, avg_time_members_v2, getDAFilePath("kept_methods"));
		AnalyzerReportUtil.loadTimeOfChangedElements(avg_time_members_v1, getDAFilePath("removed_methods"));
		AnalyzerReportUtil.loadTimeOfChangedElements(avg_time_members_v2, getDAFilePath("added_methods"));
		
		/* 
		 * From this point, we generate more details only for degradation/optimization.
		 * It is a loop for each comparison strategy property.
		 * TODO: Check if the same reports for optimization are working.
		 */
		for (String target_prefix : comparison_strategy) {
			Map<String, UpdatedMethod> p_degradation_methods = map_of_p_degradation_methods.get(target_prefix);
			Map<String, UpdatedMethod> p_optimization_methods = map_of_p_optimization_methods.get(target_prefix);
			
			if (isFilled(p_degradation_methods)) {
				// Saving potentially blamed issues. TODO: is it important?
//				AnalyzerReportUtil.saveFullMiningData(
//					"# Issues potentially blamed for performance degradation (degraded + added)",
//					getRMFilePath(target_prefix + "issues_of_performance_degradation"), p_degradation_methods,
//					AnalyzerCollectionUtil.getTaskNumbers(new HashMap<String, Collection<UpdatedMethod>>().p),
//					AnalyzerCollectionUtil.countTaskTypes(p_degradation_methods),
//					AnalyzerCollectionUtil.getTaskMembers(p_degradation_methods)
//				);
				
				// Getting and saving the impacted elements by the blamed methods
				AnalyzerReportUtil.saveImpactedElements(
					"# Methods potentially blamed for performance degradation",
					getRMFilePath(target_prefix + "methods_of_performance_degradation"), p_degradation_methods.keySet());
			}
			
			// TODO: Check if optimization is working
			if (isFilled(p_optimization_methods)) {
				/*  Saving potentially blamed issues. TODO: is it important?
				 *  The removed methods might be not considered.
				 */
//				AnalyzerReportUtil.saveFullMiningData(
//					"# Issues potentially blamed for performance optimization (removed + optimized)",
//					getRMFilePath(target_prefix + "issues_of_performance_optimization"), p_optimization_methods,
//					AnalyzerCollectionUtil.getTaskNumbers(p_optimization_methods),
//					AnalyzerCollectionUtil.countTaskTypes(p_optimization_methods),
//					AnalyzerCollectionUtil.getTaskMembers(p_optimization_methods)
//				);
	
				// Getting and saving the impacted elements by the blamed methods
				AnalyzerReportUtil.saveImpactedElements(
					"# Methods potentially blamed for performance optimization",
					getRMFilePath(target_prefix + "methods_of_performance_optimization"), p_optimization_methods.keySet());
			}
		
			Map<String, List<String>> degraded_scenario_to_blames = null;
			Map<String, List<String>> optimized_scenario_to_blames = null;
			
			// Getting the degraded scenarios and the blamed methods
			if (isFilled(p_degradation_methods)) {
				degraded_scenario_to_blames = AnalyzerCollectionUtil.getScenariosWithBlames(
					getDAFilePath(target_prefix + "degraded_scenarios"),
					getRMFilePath(target_prefix + "methods_of_performance_degradation"));
				
				removeExcludedEntryPoints(degraded_scenario_to_blames);
			}
			
			if (isFilled(p_optimization_methods)) {
				optimized_scenario_to_blames = AnalyzerCollectionUtil.getScenariosWithBlames(
					getDAFilePath(target_prefix + "optimized_scenarios"),
					getRMFilePath(target_prefix + "methods_of_performance_optimization"));
				
				removeExcludedEntryPoints(optimized_scenario_to_blames);
			}
			
			if (isFilled(p_degradation_methods)) {
				/*
				 * Showing degraded scenarios and blamed methods.
				 * Note that this save have to pass the partial name of the file.
				 * The save method will discover the path in this case.
				 */
				Set<String> blamed_commits = saveScenariosAndBlames(
						"# Methods blamed for performance degradation in each of the degraded scenarios",
						target_prefix + "blamed_methods_of_degraded_scenarios", degraded_scenario_to_blames,
						avg_time_members_v1, avg_time_members_v2, p_degradation_methods);
				
				AnalyzerReportUtil.saveCommitsForRAnalysis(repository.getAllCommits(),
						blamed_commits, getRMFilePath(target_prefix + "r_degraded_all_commits"));
				AnalyzerReportUtil.saveCommitsForRAnalysis(repository.getCommitsFromChangedMethods(),
						blamed_commits, getRMFilePath(target_prefix + "r_degraded_method_commits"));
			}
			
			// TODO: Check if optimization is working
			if (isFilled(p_optimization_methods)) {
				/*
				 * Showing optimized scenarios and blamed methods.
				 * Note that this save have to pass the partial name of the file.
				 * The save method will discover the path in this case.
				 */
				Set<String> blamed_commits = saveScenariosAndBlames(
						"# Methods blamed for performance optimization in each of the optimized scenarios",
						target_prefix + "blamed_methods_of_optimized_scenarios", optimized_scenario_to_blames,
						avg_time_members_v1, avg_time_members_v2, p_optimization_methods);
				
				AnalyzerReportUtil.saveCommitsForRAnalysis(repository.getAllCommits(),
						blamed_commits, getRMFilePath(target_prefix + "r_optimized_all_commits"));
				AnalyzerReportUtil.saveCommitsForRAnalysis(repository.getCommitsFromChangedMethods(),
						blamed_commits, getRMFilePath(target_prefix + "r_optimized_method_commits"));
			}
			
			// Maps to count how many times the classes and packages are blamed
			Map<String, Integer> total_classes = new HashMap<String, Integer>();
			Map<String, Integer> total_packages = new HashMap<String, Integer>();
			
			if (degraded_scenario_to_blames != null) {
				// Count and save the results
				countTotalOfModulesRelaxed(degraded_scenario_to_blames, total_classes, total_packages, package_deep,
						avg_time_members_v1, avg_time_members_v2);
				
				// Showing statistical for classes and packages
				AnalyzerReportUtil.saveCodeAssets("# Statistical for degraded classes and packages",
						getRMFilePath(target_prefix + "total_of_degraded_classes_and_packages_relaxed"), total_classes, total_packages);
				
				total_classes.clear();
				total_packages.clear();
				
				// Count and save the results
				countTotalOfSignificantModules(degraded_scenario_to_blames, total_classes, total_packages, package_deep,
						avg_time_members_v1, avg_time_members_v2);
				
				// Showing statistical for classes and packages
				AnalyzerReportUtil.saveCodeAssets("# Statistical for degraded classes and packages",
						getRMFilePath(target_prefix + "total_of_degraded_classes_and_packages_significant"), total_classes, total_packages);
			}
			
			if (optimized_scenario_to_blames != null) {
				// For optimization now
				total_classes.clear();
				total_packages.clear();
				
				// Count and save the results
				countTotalOfModulesRelaxed(optimized_scenario_to_blames, total_classes, total_packages, package_deep,
						avg_time_members_v1, avg_time_members_v2);
				
				// Showing statistical for classes and packages
				AnalyzerReportUtil.saveCodeAssets("# Statistical for optimized classes and packages",
						getRMFilePath(target_prefix + "total_of_optimized_classes_and_packages_relaxed"), total_classes, total_packages);
				
				total_classes.clear();
				total_packages.clear();
				
				// Count and save the results
				countTotalOfSignificantModules(optimized_scenario_to_blames, total_classes, total_packages, package_deep,
						avg_time_members_v1, avg_time_members_v2);
				
				// Showing statistical for classes and packages
				AnalyzerReportUtil.saveCodeAssets("# Statistical for optimized classes and packages",
						getRMFilePath(target_prefix + "total_of_optimized_classes_and_packages_significant"), total_classes, total_packages);
			}
		}
	}
	
	private double calcExecutionTimeAbsDelta(Double t1, Double t2) {
		if (t1 == null)
			return t2;
		else if (t2 == null)
			return t1;
		else
			return Math.abs(t2 - t1);
	}
	
	/* 
	 * This test is just for added or removed methods. The idea is not consider it always
	 * degraded or optimized. For kept methods, the statistical test are enough.
	 */
	private boolean isExecutionTimeSignificant(int count1, int count2, Double t1, Double t2) {
		double delta;
		
		// The member was added
		if (t1 == null) {
			// It is significant according the property
			delta = (count2 * t2 >= member_significance_variation)? t2 : 0;
		}
		else if (t2 == null) { // The member was removed
			// It is significant according the property
			delta = (count1 * t1 >= member_significance_variation)? t1 : 0;
		}
		else { // The member exists in both releases
			delta = (count2 * Math.abs(t2 - t1) >= member_significance_variation)? Math.abs(t2 - t1) : 0;
		}
		
		return delta >= avg_significance_delta;
	}
	
	private void countTotalOfSignificantModules(Map<String, List<String>> scenario_to_blames,
			Map<String, Integer> total_classes, Map<String, Integer> total_packages, int package_deep,
			Map<String, Double> avg_time_members_v1, Map<String, Double> avg_time_members_v2) {
		
		Set<String> counted = new HashSet<String>();
		
		for (String scenario : scenario_to_blames.keySet()) {
			for (String sig : scenario_to_blames.get(scenario)) {
				Double t1 = avg_time_members_v1.get(sig);
				Double t2 = avg_time_members_v2.get(sig);
				
				System.out.println("Scenarios and blames: Counting " + sig + " in R1 for " + scenario);
				int count1 = DatabaseRelease.getDatabasev1().countMethodExecutionByScenario(scenario, sig);
				System.out.println("\tTotal = " + count1);
				
				System.out.println("Scenarios and blames: Counting " + sig + " in R2 for " + scenario);
				int count2 = DatabaseRelease.getDatabasev2().countMethodExecutionByScenario(scenario, sig);
				System.out.println("\tTotal = " + count2);
				
				if (!counted.contains(sig) && !matchesExcludeWord(sig) && isExecutionTimeSignificant(count1, count2, t1, t2)) {
					String class_name = getClassNameFromSignature(sig);
					String package_prefix = getPackagePrefixFromSignature(sig, package_deep);
					
					Integer value = total_classes.get(class_name);
					total_classes.put(class_name, value == null ? 1 : value + 1);
					
					value = total_packages.get(package_prefix);
					total_packages.put(package_prefix, value == null ? 1 : value + 1);
					
					counted.add(sig);
				}
			}
		}
		
	}
	
	private void countTotalOfModulesRelaxed(Map<String, List<String>> scenario_to_blames,
			Map<String, Integer> total_classes, Map<String, Integer> total_packages, int package_deep,
			Map<String, Double> avg_time_members_v1, Map<String, Double> avg_time_members_v2) {
		
		Set<String> counted = new HashSet<String>();
		
		for (String scenario : scenario_to_blames.keySet()) {
			for (String sig : scenario_to_blames.get(scenario)) {
				Double t1 = avg_time_members_v1.get(sig);
				Double t2 = avg_time_members_v2.get(sig);
				double delta = calcExecutionTimeAbsDelta(t1, t2);
				
				if (!counted.contains(sig) && !matchesExcludeWord(sig) && delta >= avg_significance_delta) {
					String class_name = getClassNameFromSignature(sig);
					String package_prefix = getPackagePrefixFromSignature(sig, package_deep);
					
					Integer value = total_classes.get(class_name);
					total_classes.put(class_name, value == null ? 1 : value + 1);
					
					value = total_packages.get(package_prefix);
					total_packages.put(package_prefix, value == null ? 1 : value + 1);
					
					counted.add(sig);
				}
			}
		}
		
	}
	
	private void removeExcludedEntryPoints(Map<String, List<String>> scenario_to_blames) {
		for (String entry : exclude_entry_points)
			scenario_to_blames.remove(entry);
	}
	
	private String getClassNameFromSignature(String signature) {
		String method_name = signature.substring(0, signature.indexOf("("));
		
		if (Character.isLowerCase(method_name.charAt(method_name.lastIndexOf('.') + 1)))
			return method_name.substring(0, method_name.lastIndexOf("."));
		
		return method_name;
	}
	
	private String getPackagePrefixFromSignature(String signature, int deep) {
		int i = 0, j = 0;
		
		while (i < signature.length()) {
			if (signature.charAt(i) == '.')
				++j;
			
			if (j == deep)
				break;
			
			++i;
		}
		
		return signature.substring(0, i);
	}
	
	private boolean matchesExcludeWord(String text) {
		return (exclude_word != null && !exclude_word.isEmpty() && text.contains(exclude_word));
	}
	
	private Collection<String> matchesName(String path, String method_name, Collection<String> method_signatures) {
		Collection<String> signatures = new ArrayList<String>();
		
		String class_name = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
		String class_dot_method = class_name + "." + method_name;
		
		for (String s : method_signatures)
			if (s.matches(".*[.]" + class_dot_method + "[(].*"))
				signatures.add(s);
		
		return signatures;
	}

}
