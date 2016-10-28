package br.ufrn.ppgsc.scenario.analyzer.miner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IPathTransformer;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.AbstractStatElement;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.DoubleStatElement;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.SimpleStatElement;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerReportUtil;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerStatistical.Tests;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.RepositoryManager;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public final class AnalyzerMinerByScenarioRepositoryRunnable {

	private IPathTransformer transformer;
	private double alpha_significance_level;
	
	private String repository_url;
	private String repository_prefix;
	private String repository_user;
	private String repository_password;
	private String system_id;
	private String strdate;
	private String workcopy_prefix_r1;
	private String workcopy_prefix_r2;
	
	private String[] exclude_entry_points;
	
	private double avg_significance_delta;
	private double member_significance_variation;

	public AnalyzerMinerByScenarioRepositoryRunnable(String strdate) throws FileNotFoundException {
		SystemMetadataUtil properties = SystemMetadataUtil.getInstance();
		
		alpha_significance_level = properties.getDoubleProperty("alpha_significance_level");
		transformer = properties.newObjectFromProperties(IPathTransformer.class);
		this.strdate = strdate;
		
		repository_url = properties.getStringProperty("repository_url");
		repository_prefix = properties.getStringProperty("repository_prefix");
		repository_user = properties.getStringProperty("repository_user");
		repository_password = properties.getStringProperty("repository_password");
		system_id = properties.getStringProperty("system_id");
		
		workcopy_prefix_r1 = properties.getStringProperty("workcopy_prefix_r1");
		workcopy_prefix_r2 = properties.getStringProperty("workcopy_prefix_r2");
		
		avg_significance_delta = properties.getDoubleProperty("avg_significance_delta");
		member_significance_variation = properties.getDoubleProperty("member_significance_variation");
		
		exclude_entry_points = properties.getStringProperty("exclude_entry_points").split(";");
	}
	
	// TODO: How can I move this to report util?
	private String getDAFilePath(String partial_name) {
		return "reports/degradation_analysis/" + system_id + "_" + partial_name + "_" + strdate + ".txt";
	}
	
	// TODO: How can I move this to report util?
	private String getRMFilePath(String partial_name) {
		return "reports/repository_mining/" + system_id + "_" + partial_name + "_" + strdate + ".txt";
	}
	
	/* 
	 * TODO: Check if it is true!
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
	
	
	private Set<String> removeExcludedEntryPoints(Set<String> scenarios) {
		for (String entry : exclude_entry_points)
			scenarios.remove(entry);
		
		return scenarios;
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

	private <K, V extends AbstractStatElement> Set<String> getSignaturesOfMembers(
			Map<K, List<V>> from_scenarios_to_members) {
		Set<String> signatures_of_members = new HashSet<String>();

		for (List<V> elements : from_scenarios_to_members.values()) {
			for (AbstractStatElement e : elements) {
				signatures_of_members.add(e.getElementName());
			}
		}

		return signatures_of_members;
	}

	private <T extends AbstractStatElement> Collection<T> filterStatElementsByNameAndTime(Collection<T> elements,
			Set<String> names_of_interest) {
		Collection<T> result = new ArrayList<T>();

		if (elements != null) {
			for (T e : elements) {
				//if (!AnalyzerCollectionUtil.matchesExcludeWord(e.getElementName()) && names_of_interest.contains(e.getElementName())) {
				if (names_of_interest.contains(e.getElementName())) {
					if (e instanceof SimpleStatElement) {
						SimpleStatElement simple_e = (SimpleStatElement) e;
						
						if (isExecutionTimeSignificant(simple_e.getNumberOfExecutions(), 0, simple_e.getAverage(), null))
							result.add(e);
					}
					else {
						DoubleStatElement double_e = (DoubleStatElement) e;
						
						if (isExecutionTimeSignificant(double_e.getNumberOfExecutionsV1(), double_e.getNumberOfExecutionsV2(), double_e.getAverageV1(), double_e.getAverageV2()))
							result.add(e);
					}
				}
			}
		}

		return result;
	}
	
	private void runForFile(String report_file, RepositoryManager repository) throws IOException {
		Map<String, List<DoubleStatElement>> from_scenarios_to_kept_members = new HashMap<String, List<DoubleStatElement>>();
		Map<String, List<SimpleStatElement>> from_scenarios_to_added_members = new HashMap<String, List<SimpleStatElement>>();
		Map<String, List<SimpleStatElement>> from_scenarios_to_removed_members = new HashMap<String, List<SimpleStatElement>>();
		Map<String, DoubleStatElement> scenarios = new HashMap<String, DoubleStatElement>();
		
		AnalyzerReportUtil.loadReportByScenario(
				from_scenarios_to_kept_members,
				from_scenarios_to_added_members,
				from_scenarios_to_removed_members,
				scenarios, getDAFilePath(report_file));
		
		Set<String> every_signature_of_members = new HashSet<String>();
		every_signature_of_members.addAll(getSignaturesOfMembers(from_scenarios_to_kept_members));
		every_signature_of_members.addAll(getSignaturesOfMembers(from_scenarios_to_added_members));
		every_signature_of_members.addAll(getSignaturesOfMembers(from_scenarios_to_removed_members));
		
		// Path are always to files
		List<String> repository_paths = new ArrayList<String>();
		List<String> old_workcopy_paths = new ArrayList<String>();
		List<String> new_workcopy_paths = new ArrayList<String>();
		
		for (String signature : every_signature_of_members) {
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
		
		System.out.println("Getting updated methods from repository to degraded methods [" 	+ repository_paths.size() + "]");
		
		/*
		 * Todos os caminhos de classes que possuem métodos com variação.
		 * Serão coletadas todas as atualizações de métodos dessas classes.
		 */
		Map<String, Collection<UpdatedMethod>> from_every_classpath_to_upmethod = null;
		
		if (!every_signature_of_members.isEmpty())
			from_every_classpath_to_upmethod = repository.getUpdatedMethodsFromRepository(repository_paths, old_workcopy_paths, new_workcopy_paths);
		
		if (from_every_classpath_to_upmethod == null)
			from_every_classpath_to_upmethod = new HashMap<String, Collection<UpdatedMethod>>();
		
		// This contains the signature of methods modified and with variation
		Set<String> filtered_signature_of_members = new HashSet<String>();
		
		// Check every path to filter the collection of methods
		for (String path : from_every_classpath_to_upmethod.keySet()) {
			// Check every method of the path
			for (UpdatedMethod upm : from_every_classpath_to_upmethod.get(path)) {
				/* 
				 * Check if the method name matches with the method signature.
				 * There is a limitation when the method has the same name with
				 * different parameters. This is caused because of the JDT parser.
				 * TODO: Resolve the JDT parser problem to recovery the full name of
				 * methods, including their parameters.
				 * Every signature which matches with the method name that was modified.
				 */
				Collection<String> matched_signatures = matchesName(path, upm.getMethodLimit().getSignature(), every_signature_of_members);
				filtered_signature_of_members.addAll(matched_signatures);
			}
		}
		
		System.out.println(filtered_signature_of_members.size());
		for (String s : filtered_signature_of_members) {
			System.out.println(s);
		}
		
		int i = 0;
		for (String scenario_name : removeExcludedEntryPoints(scenarios.keySet())) {
			// Filtered are the methods which have deviation and also have modification 
			
			Collection<DoubleStatElement> filtered_kept_members = filterStatElementsByNameAndTime(
					from_scenarios_to_kept_members.get(scenario_name), filtered_signature_of_members);
			
			Collection<SimpleStatElement> filtered_added_members = filterStatElementsByNameAndTime(
					from_scenarios_to_added_members.get(scenario_name), filtered_signature_of_members);
			
			Collection<SimpleStatElement> filtered_removed_members = filterStatElementsByNameAndTime(
					from_scenarios_to_removed_members.get(scenario_name), filtered_signature_of_members);
			
			DoubleStatElement compared_scenario = scenarios.get(scenario_name);

			if (!filtered_kept_members.isEmpty() || !filtered_added_members.isEmpty() || !filtered_removed_members.isEmpty()) {
				AnalyzerReportUtil.saveDoubleElementsOfScenario(
						"# " + ++i + " Members with deviation and modification for scenario " + scenario_name,
						getRMFilePath("modified_with_" + report_file), compared_scenario,
						Tests.UTest, alpha_significance_level, filtered_kept_members);
				
				AnalyzerReportUtil.saveSimpleElements(
						"# " + i + " Added and modified members in the execution of scenario " + scenario_name,
						getRMFilePath("modified_with_" + report_file), filtered_added_members);
				
				AnalyzerReportUtil.saveSimpleElements(
						"# " + i + " Removed and modified members in the execution of scenario " + scenario_name,
						getRMFilePath("modified_with_" + report_file), filtered_removed_members);
			}
		}
	}
	
	public void run() throws IOException {
		// It connects to the repository at the time it is created
		RepositoryManager repository = new RepositoryManager(repository_url, repository_user, repository_password);
		runForFile("deviation_members_by_degraded_scenarios", repository);
		runForFile("deviation_members_by_optimized_scenarios", repository);
	}

}
