package br.ufrn.ppgsc.scenario.analyzer.miner;

import java.io.File;
import java.io.FileInputStream;
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
import java.util.Scanner;
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
		
		for (String name : TARGET_FILES)
			if (properties.getBooleanProperty(name))
				active_targets.add(name);
		
	}
	
	private Map<String, List<String>> getScenariosWithBlames(String scenarios_file, String methods_file) throws FileNotFoundException {
		Map<String, List<String>> scenarios_blames = new HashMap<String, List<String>>();
		
		Scanner in = new Scanner(new FileInputStream(getOutputFileName(methods_file)));
		
		loadScenarios(scenarios_blames, scenarios_file);
		
		// Ler a mensagem
		System.out.println("Processing... " + in.nextLine());
		
		int total_members = in.nextInt(); in.nextLine();
		
		for (int i = 0; i < total_members; i++) {
			
			String signature = in.nextLine();
			int total_scenarios = in.nextInt(); in.nextLine();
			
			for (int j = 0; j < total_scenarios; j++) {
				
				String scenario = in.nextLine();
				
				List<String> members_sig = scenarios_blames.get(scenario);
				if (members_sig != null)
					members_sig.add(signature);
				
			}
			
		}

		in.close();
		
		return scenarios_blames;
	}
	
	private String loadScenarios(Map<String, List<String>> scenarios_blames, String name) throws FileNotFoundException {
		String message = null;
		String filename = getOutputFileName(name);
		
		Scanner in = new Scanner(new FileInputStream(filename));

		message = in.nextLine();
		
		int total = in.nextInt();
		in.nextLine();

		for (int i = 0; i < total; i++) {
			String scenario_name = in.nextLine();
			in.nextLine(); // Tempos
			scenarios_blames.put(scenario_name, new ArrayList<String>());
		}

		in.close();

		return message;
	}
	
	public void persistFile(String message, String filename,
			Map<String, Integer> total_classes, Map<String, Integer> total_packages) throws FileNotFoundException {
		System.out.println("persistFile: " + message);
		
		PrintWriter pw = new PrintWriter(getOutputFileName(filename));
		
		pw.println(total_classes.size());
		
		Set<String> class_keys = new TreeSet<String>(total_classes.keySet());
		Set<String> package_keys = new TreeSet<String>(total_packages.keySet());
		
		for (String class_name : class_keys)
			pw.println(class_name + " " + total_classes.get(class_name));
		
		pw.println(total_packages.size());
		
		for (String package_prefix : package_keys)
			pw.println(package_prefix + " " + total_packages.get(package_prefix));
		
		pw.close();
	}
	
	public void countTotalOfModules(Map<String, List<String>> scenariosWithBlames,
			Map<String, Integer> total_classes, Map<String, Integer> total_packages, int package_deep,
			Map<String, Double> avg_time_members_v1, Map<String, Double> avg_time_members_v2) {
		
		Set<String> counted = new HashSet<String>();
		
		for (List<String> signatures : scenariosWithBlames.values()) {
			if (signatures.isEmpty())
				continue;
			
			for (String sig : signatures) {
				Double t1 = avg_time_members_v1.get(sig);
				Double t2 = avg_time_members_v2.get(sig);
				
				double delta = t1 == null ? t2 : t2 - t1;
				
				if (counted.contains(sig) || matchesExcludeWord(sig) || delta < 0.001)
					continue;
				
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
	
	private void persistScenariosWithBlames(String message, String filename, Map<String, List<String>> scenariosWithBlames,
			Map<String, Double> avg_time_members_v1, Map<String, Double> avg_time_members_v2,
			Map<String, Collection<UpdatedMethod>> p_degraded_changed_methods) throws FileNotFoundException {
		int total_members = 0;
		int total_scenarios_with_blames = 0;
		int total_members_without_word = 0;
		
		Set<String> members_with_time = new TreeSet<String>();
		Set<String> counted = new HashSet<String>();
		
		Map<Long, Issue> map_number_issue = new HashMap<Long, Issue>();
		
		System.out.println("persistFile: " + message);
		
		PrintWriter pw = new PrintWriter(getOutputFileName(filename));
		
		PrintWriter pwl = new PrintWriter(getOutputFileName(filename + "_list_"));
		
		for (List<String> list : scenariosWithBlames.values())
			if (!list.isEmpty())
				++total_scenarios_with_blames;
		
		pw.println(message);
		pwl.println(message + " [list]");
		
		// Cenários que possuem blames
		pw.println(total_scenarios_with_blames);
		pwl.println(total_scenarios_with_blames);
		
		/* 
		 * Todos os cenários, alguns podem não ter blames.
		 * Isso acontece, por exemplo, devido somatórios de membros
		 * degradados, pequenos aumentos individuais (menor que a taxa)
		 * que quando somados impactam o cenário (com variação maior que a taxa)
		 */
		pw.println(scenariosWithBlames.size());
		
		for (String scenario : new TreeSet<String>(scenariosWithBlames.keySet())) {
			List<String> signatures = scenariosWithBlames.get(scenario);
			
			Collections.sort(signatures);
			
			pw.println(scenario);
			
			int i = 0;
			for (String s : signatures)
				if (!matchesExcludeWord(s))
					++i;
			
			pw.println(signatures.size() + " " + i);
			
			if (i > 0)
				pwl.println(scenario);
			
			for (String s : signatures) {
				Map<Long, Issue> local_map_number_issue = new HashMap<Long, Issue>();
				
				Double t1 = avg_time_members_v1.get(s);
				Double t2 = avg_time_members_v2.get(s);
				
				double delta = t1 == null ? t2 : t2 - t1;
				
				String text = s + " " + t1 + " " + t2 + " " + delta;
				
				for (UpdatedMethod um : p_degraded_changed_methods.get(s)) {
					for (UpdatedLine ul : um.getUpdatedLines()) {
						for (Issue issue  : ul.getIssues()) {
							local_map_number_issue.put(issue.getNumber(), issue);
						}
					}
				}
				
				for (Issue issue : local_map_number_issue.values())
					text += " " + issue.getNumber();
				
				pw.println(text);
				// Aqui vai contar com os testes
				//members_with_time.add(text);
				//map_number_issue.putAll(local_map_number_issue);
				
				if (!counted.contains(s)) {
					counted.add(s);
					++total_members;
					
					if (!matchesExcludeWord(s) && delta >= 0.001) {
						++total_members_without_word;
						members_with_time.add(text);
						map_number_issue.putAll(local_map_number_issue);
					}
				}
			}
		}
		
		pw.println(total_members);
		pw.println(total_members_without_word);
		
		pwl.println(members_with_time.size());
		for (String member : members_with_time)
			pwl.println(member);
		
		pwl.println(map_number_issue.size());
		for (long issue_number : new TreeSet<Long>(map_number_issue.keySet()))
			pwl.println(issue_number + ":" + map_number_issue.get(issue_number).getIssueType());
		
		Map<String, Set<Long>> map_type_issues = new HashMap<String, Set<Long>>();
		for (Issue issue  : map_number_issue.values()) {
			Set<Long> issue_number_list = map_type_issues.get(issue.getIssueType());
			
			if (issue_number_list == null) {
				issue_number_list = new TreeSet<Long>();
				map_type_issues.put(issue.getIssueType(), issue_number_list);
			}
			
			issue_number_list.add(issue.getNumber());
		}
		
		pwl.println(map_type_issues.size());
		for (String issue_type : map_type_issues.keySet())
			pwl.println(issue_type + ":" + map_type_issues.get(issue_type).size());
		
		pwl.println(map_type_issues.size());
		for (String issue_type : map_type_issues.keySet()) {
			pwl.print(issue_type + ":");
			for (long numbers : map_type_issues.get(issue_type))
				pwl.print(numbers + " ");
			pwl.println();
		}
		
		pw.close();
		pwl.close();
	}
	
	private String getInputFileName(String partial_name) {
		return "reports/degradation_analysis/" + system_id + "_" + partial_name + "_" + strdate + ".txt";
	}
	
	private String getOutputFileName(String partial_name) {
		return "reports/repository_mining/" + system_id + "_" + partial_name + "_" + strdate + ".txt";
	}
	
	public void run() throws IOException {
		// It connects to the repository at the time it is created
		RepositoryManager repository = new RepositoryManager(repository_url, repository_user, repository_password);
		
		// Methods that have contributed to performance degradation. The key is the method signature
		Map<String, Collection<UpdatedMethod>> p_degradation_methods = new HashMap<String, Collection<UpdatedMethod>>();
		
		// Methods that have contributed to performance optimization. The key is the method signature
		Map<String, Collection<UpdatedMethod>> p_optimization_methods = new HashMap<String, Collection<UpdatedMethod>>();
		
		for (String filename : active_targets) {
			List<String> repository_paths = new ArrayList<String>();
			List<String> old_workcopy_paths = new ArrayList<String>();
			List<String> new_workcopy_paths = new ArrayList<String>();
			
			/*
			 * It will be methods and their statistical. The keys are their signatures.
			 * We will have to filter the unchanged methods of this collection.
			 */
			Collection<String> full_signatures = new ArrayList<String>();
			
			String file_message = AnalyzerReportUtil.loadSignatures(full_signatures, getInputFileName(filename));
			
			for (String signature : full_signatures) {
				String paths[] = transformer.convert(signature, repository_prefix, workcopy_prefix_r1, workcopy_prefix_r2);
				
				if (paths == null) {
					System.out.println("Ignoring " + signature + " because paths is null (Is it right?).");
					continue;
				}
				
				boolean ignore_file = false;
				boolean file_exists_r1 = new File(paths[1]).exists();
				boolean file_exists_r2 = new File(paths[2]).exists();
				
				if (!file_exists_r1 && !file_exists_r2)
					throw new FileNotFoundException("\n" + paths[1] + "\n" + paths[2]);
				
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
					String matched_signature = matchesName(path, upm.getMethodLimit().getSignature(), full_signatures);
					
					if (matched_signature != null) {
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
						if (filename.equals("added_methods") || filename.endsWith("degraded_methods"))
							p_degradation_methods.put(matched_signature, collection);
						
						if (filename.equals("removed_methods") || filename.endsWith("optimized_methods"))
							p_optimization_methods.put(matched_signature, collection);
					}
				}
			}
			
			// Collect some issue information for the full list of changed methods
			Map<String, Collection<UpdatedMethod>> full_issuetype_to_upmethod = AnalyzerCollectionUtil.getTaskMembers(full_path_to_upmethod);
			Map<String, Integer> full_issuetype_to_count = AnalyzerCollectionUtil.counterTaskTypes(full_path_to_upmethod);
			Collection<Long> full_issue_numbers = AnalyzerCollectionUtil.getTaskNumbers(full_path_to_upmethod);
			
			// Collect some issue information for the filtered list of changed methods
			Map<String, Collection<UpdatedMethod>> filtered_issuetype_to_upmethod = AnalyzerCollectionUtil.getTaskMembers(filtered_path_to_upmethod);
			Map<String, Integer> filtered_issuetype_to_count = AnalyzerCollectionUtil.counterTaskTypes(filtered_path_to_upmethod);
			Collection<Long> filtered_issue_numbers = AnalyzerCollectionUtil.getTaskNumbers(filtered_path_to_upmethod);
			
			// Save the results of all methods from degradation analysis
			AnalyzerReportUtil.saveReport(file_message + " (after mining phase)", getOutputFileName("full_" + filename), full_path_to_upmethod,
					full_issue_numbers,	full_issuetype_to_count, full_issuetype_to_upmethod);
			
			// Save the results of the filtered methods (degraded and changed)
			AnalyzerReportUtil.saveReport(file_message, getOutputFileName("filtrated_" + filename), filtered_path_to_upmethod,
					filtered_issue_numbers,	filtered_issuetype_to_count, filtered_issuetype_to_upmethod);
		}
		
		if (!p_degradation_methods.isEmpty()) {
			// Saving issues
			AnalyzerReportUtil.saveReport("# Issues potentially blamed for performance degradation",
					"issues_performance_degradation", p_degradation_methods);
			
			// Getting and saving the impacted elements by the blamed methods
			AnalyzerReportUtil.getImpactedElementsAndsaveReport("# Methods potentially blamed for performance degradation",
					"methods_performance_degradation", p_degradation_methods.keySet());
		}
		
		// TODO: Check if optimization is working
		if (!p_optimization_methods.isEmpty()) {
			// Saving issues
			AnalyzerReportUtil.saveReport("# Issues potentially blamed for performance optimization",
					"issues_performance_degradation", p_optimization_methods);
			
			// Getting and saving the impacted elements by the blamed methods
			AnalyzerReportUtil.getImpactedElementsAndsaveReport("# Methods potentially blamed for performance optimization",
					"methods_performance_degradation", p_optimization_methods.keySet());
		}
		
		// Getting the degraded scenarios and the blamed methods
		Map<String, List<String>> scenariosWithBlames = getScenariosWithBlames("p_degraded_scenarios", "methods_performance_degradation");
		
		/*
		 * I removed these tests because they changed.
		 * TODO: Think how to include the possibility of excluding scenarios.
		 */
		scenariosWithBlames.remove("Entry point for DatagramUnicastTest.testSimpleSend");
		scenariosWithBlames.remove("Entry point for SocketConnectionAttemptTest.testConnectTimeout");
		scenariosWithBlames.remove("Entry point for DatagramMulticastTest.testMulticast");
		
		/*
		 * The same case, but now for ArgoUML. This scenario has a sleep(2000).
		 * TODO: Think how to include the possibility of excluding scenarios.
		 */		
		scenariosWithBlames.remove("Entry point for TestNotationProvider.testListener");
		
		// TODO: Can I reuse it?
		Map<String, Double> avg_time_members_v1 = DatabaseRelease.getDatabasev1().getExecutionTimeAverageOfMembers();
		Map<String, Double> avg_time_members_v2 = DatabaseRelease.getDatabasev2().getExecutionTimeAverageOfMembers();
		
		if (!p_degradation_methods.isEmpty()) {
			// Showing degraded scenarios and blamed methods
			persistScenariosWithBlames("# Methods blamed for performance degradation in each of the degraded scenarios",
					"scenarios_blames_performance_degradation", scenariosWithBlames,
					avg_time_members_v1, avg_time_members_v2, p_degradation_methods);
		}
		
		// TODO: Check if optimization is working
		if (!p_optimization_methods.isEmpty()) {
			// Showing optimized scenarios and blamed methods
			persistScenariosWithBlames("# Methods blamed for performance optimization in each of the optimized scenarios",
					"scenarios_blames_performance_degradation", scenariosWithBlames,
					avg_time_members_v1, avg_time_members_v2, p_optimization_methods);
		}
		
		// Maps to count how many times the classes and packages are blamed
		Map<String, Integer> total_classes = new HashMap<String, Integer>();
		Map<String, Integer> total_packages = new HashMap<String, Integer>();
		
		// Count and save the results
		countTotalOfModules(scenariosWithBlames, total_classes, total_packages, package_deep,
				avg_time_members_v1, avg_time_members_v2);
		
		// Shoing the scenaios and the blamed methods
		persistFile("# Contagem de classes e pacotes", "total_of_classes_and_packages", total_classes, total_packages);
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
	
	private String matchesName(String path, String method_name, Collection<String> method_signatures) {
		String class_name = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
		String class_dot_method = class_name + "." + method_name;
		
		for (String s : method_signatures)
			if (s.matches(".*[.]" + class_dot_method + "[(].*"))
				return s;
		
		return null;
	}

}
