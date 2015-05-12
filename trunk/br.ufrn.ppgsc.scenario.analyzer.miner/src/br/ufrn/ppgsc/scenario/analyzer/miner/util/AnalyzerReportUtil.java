package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeGenericAnnotation;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.DatabaseRelease;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDB;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Commit;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.StatElement;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;

public abstract class AnalyzerReportUtil {

	public static void saveAVGs(
			String message, String filename,
			Map<String, Double> avgs, Collection<String> elements) throws FileNotFoundException {
		
		System.out.println("Saving >> " + message);

		PrintWriter pw = new PrintWriter(new FileOutputStream(filename));

		pw.println(message);
		pw.println(elements.size());
		pw.println("Name;Average");

		for (String key : elements)
			pw.println(key + ";" + avgs.get(key));
		
		pw.close();
		
	}
	
	public static void saveElements(
			String message, String filename,
			Collection<StatElement> collection,
			double rate, double alpha) throws FileNotFoundException {
		
		System.out.println("Saving >> " + message);

		PrintWriter pw = new PrintWriter(new FileOutputStream(filename));

		pw.println(message);
		pw.println(collection.size());
		pw.println("Name;P-Value (TTest);P-Value (UTest);Mean R1;Mean R2;N1;N2");

		for (StatElement e : collection) {
			pw.println(e.getElementName() + ";"
					+ e.getTTestPvalue() + ";" + e.getUTestPvalue() + ";"
					+ e.getAVGv1() + ";" + e.getAVGv2() + ";"
					+ e.getN1() + ";" + e.getN2());
		}
		
		pw.println(rate);
		pw.println(alpha);
		
		pw.close();
		
	}
	
	public static void saveFails(String message, String filename,
			Map<RuntimeScenario, List<RuntimeNode>> scenario_to_nodes,
			Map<String, Integer> failed_methods, int release) throws FileNotFoundException {
		
		System.out.println("Saving >> " + message);
		
		GenericDB db;
		
		if (release == 1)
			db = DatabaseRelease.getDatabasev1();
		else if (release == 2)
			db = DatabaseRelease.getDatabasev2();
		else
			throw new RuntimeException("Wrong release at runtime, aborting...");
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(filename));
		
		pw.println(message);
		pw.println(scenario_to_nodes.size());
		
		for (RuntimeScenario s : scenario_to_nodes.keySet()) {
			List<RuntimeNode> nodes = scenario_to_nodes.get(s);
			
			pw.print(s.getId() + ",");
			pw.print(s.getName() + ",");
			pw.print(s.getThreadId() + ",");
			pw.print(s.getDate() + ",");
			pw.println(s.getRoot().getMemberSignature());
			
			pw.println(s.getContext().size());

			for (String key : s.getContext().keySet())
				pw.println(key + "=" + s.getContext().get(key));
			
			pw.println(nodes.size());
			
			for (RuntimeNode n : nodes) {
				String sig = n.getMemberSignature();
				
				pw.print(n.getId() + ",");
				pw.print(sig + ",");
				pw.print(n.getExecutionTime() + ",");
				pw.print(n.getExceptionMessage() + ",");
				pw.println((double) failed_methods.get(sig) / db.countGeneralMethodExecution(sig));
				
				pw.println(n.getAnnotations().size());
				
				for (RuntimeGenericAnnotation annotation : n.getAnnotations())
					pw.println(annotation.getName());
			}
			
			pw.println("----------------------------------------------------------------------");
		}
		
		pw.close();
		
	}
	
	public static void saveCollection(String message, String filename, Collection<String> collection, boolean hasHeader) throws FileNotFoundException {
		System.out.println("Saving >> " + message);
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(filename));
		
		pw.println(message);
		
		if (hasHeader)
			pw.println(collection.size() - 1);
		else
			pw.println(collection.size());
		
		for (String elem : collection)
			pw.println(elem);
		
		pw.close();
	}
	
	public static void saveFullMiningData(String message, String filename,
			Map<String, Collection<UpdatedMethod>> path_to_upmethod,
			Collection<Long> issue_numbers,
			Map<String, Integer> issuetype_to_count,
			Map<String, Collection<UpdatedMethod>> issuetype_to_upmethod) throws FileNotFoundException {
		
		System.out.println("Saving >> " + message);
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(filename));
		
		pw.println(message);
		pw.print("Number of Issues: ");
		
		StringBuilder sb = new StringBuilder();
		for (Long number : issue_numbers) {
			if (number > 0) {
				sb.append(number);
				sb.append(";");
			}
		}
		if (sb.length() > 0)
			sb.deleteCharAt(sb.length() - 1);
		pw.println(sb.toString());
		
		pw.println("Number of paths: " + path_to_upmethod.size());
		
		for (String path: path_to_upmethod.keySet()) {
			Collection<UpdatedMethod> upmethods_collection = path_to_upmethod.get(path);
			
			pw.println(path);
			pw.println("\tNumber of methods: " + upmethods_collection.size());
			
			for (UpdatedMethod upmethod : upmethods_collection) {
				Collection<UpdatedLine> uplines_collection = upmethod.getUpdatedLines();
				
				pw.println("\t" + upmethod.getMethodLimit().getSignature() + ";" +
						upmethod.getMethodLimit().getStartLine() + ";" +
						upmethod.getMethodLimit().getEndLine());
				
				pw.println("\t\tNumber of updated lines: " + uplines_collection.size());
				
				for (UpdatedLine upline : uplines_collection) {
					Collection<Issue> issue_collection = upline.getCommit().getIssues();
				
					pw.println("\t\tLine Number: " + upline.getLineNumber());
					pw.println("\t\tCommit: " + upline.getCommit().getRevision());
					pw.println("\t\tNumber of Issues: " + issue_collection.size());
					
					for (Issue issue : issue_collection)
						pw.println("\t\t\t" + issue.getId() + ";" + issue.getNumber() + ";" + issue.getType());
				}
			}
		}
		
		pw.println("Number of Types of Issues: " + issuetype_to_count.size());
		
		for (String type : issuetype_to_count.keySet())
			pw.println("\t" + type + ";" + issuetype_to_count.get(type));
		
		pw.println("Number of Types of Issues: " + issuetype_to_upmethod.size());

		for (String type : issuetype_to_upmethod.keySet()) {
			Collection<UpdatedMethod> upmethod_collection = issuetype_to_upmethod.get(type);

			pw.println("\t" + type);
			pw.println("\t\tNumber of Methods: " + upmethod_collection.size());
			
			for (UpdatedMethod upm : upmethod_collection)
				pw.println("\t\t" + upm.getMethodLimit().getSignature());
		}
		
		pw.close();
	}
	
	public static void saveImpactedElements(String message, String filename,
			Collection<String> methods) throws FileNotFoundException {
		System.out.println("Saving >> " + message);
		
		GenericDB database_v2 = DatabaseRelease.getDatabasev2();
		PrintWriter pw = new PrintWriter(filename);
		
		pw.println(message);
		pw.println(methods.size());
		
		int i = 0;
		for (String signature : methods) {
			System.out.println("[" + ++i + "/" + methods.size() + "] Retrieving impacted members and scenarios by " + signature);
			
//			Set<String> nodes = database_v2.getImpactedNodes(signature);
			List<String> scenarios = database_v2.getScenariosByMember(signature);
			
//			pw.println(signature + ":" + nodes.size());
			pw.println(signature);
			pw.println(scenarios.size());
			
			for (String s : scenarios)
				pw.println(s);
		}
		
		pw.close();
	}
	
	public static void saveCodeAssets(String message, String filename,
			Map<String, Integer> total_classes, Map<String, Integer> total_packages) throws FileNotFoundException {
		System.out.println("Saving >> " + message);
		
		PrintWriter pw = new PrintWriter(filename);
		
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
	
	public static String loadCollection(Collection<String> collection, String filename, boolean header) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		
		String message = br.readLine();
		System.out.println("Loading >> " + message);
		
		int number_of_registers = Integer.parseInt(br.readLine());
		
		if (header)
			br.readLine();
		
		for (int i = 0; i < number_of_registers; i++)
			collection.add(br.readLine().split(";")[0]); // I just need the first token: element name
		
		/* 
		 * This method should read the performance rate and the significance level here.
		 * However I do not need them at this moment. So, I will just close the reader.
		 */
		br.close();
		
		System.out.println("\tTotal = " + collection.size());
		
		return message;
	}
	
	public static String loadTimeOfChangedElements(Map<String, Double> signature_to_avg, String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		
		String message = br.readLine();
		System.out.println("Loading >> " + message);
		
		int number_of_registers = Integer.parseInt(br.readLine());
		
		// Just read the header = Name;Average
		br.readLine();
		
		for (int i = 0; i < number_of_registers; i++) {
			String[] line = br.readLine().split(";");
			signature_to_avg.put(line[0], Double.parseDouble(line[1]));
		}
		
		br.close();
		
		System.out.println("\tTotal = " + signature_to_avg.size());
		
		return message;
	}
	
	public static String loadTimeOfKeptElements(
			Map<String, Double> signature_to_avgr1, Map<String, Double> signature_to_avgr2, 
			String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		
		String message = br.readLine();
		System.out.println("Loading >> " + message);
		
		int number_of_registers = Integer.parseInt(br.readLine());
		
		/*
		 * Just read the header = Name;P-Value (TTest);P-Value (UTest);Mean R1;Mean R2;N1;N2
		 *                        0    1               2               3       4       5  6
		 */
		br.readLine();
		
		for (int i = 0; i < number_of_registers; i++) {
			String[] line = br.readLine().split(";");
			signature_to_avgr1.put(line[0], Double.parseDouble(line[3]));
			signature_to_avgr2.put(line[0], Double.parseDouble(line[4]));
		}
		
		/* 
		 * This method should read the performance rate and the significance level here.
		 * However I do not need them at this moment. So, I will just close the reader.
		 */
		br.close();
		
		System.out.println("\tTotal = " + signature_to_avgr1.size() + " and " + signature_to_avgr2.size());
		
		return message;
	}
	
	public static List<String> loadFile(String filename) throws IOException {
		List<String> result = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(filename));
		
		String line = null;
		while ((line = br.readLine()) != null)
			result.add(line);
		
		br.close();
		return result;
	}

	public static void saveCommitsForRAnalysis(Set<Commit> all_commits, Set<String> blamed_commits, String filename, String separator) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(filename);
		Set<String> lines = new TreeSet<String>(); // A TreeSet, just to keep the order

		if (blamed_commits == null)
			blamed_commits = new HashSet<String>();
		
		for (Commit commit : all_commits)
			lines.add(blamed_commits.contains(commit.getRevision()) + separator + AnalyzerCollectionUtil.getCommitSelectedProperties(commit, separator));
		
		// Number of commits
		pw.println(lines.size());
		
		// Header
		pw.print("Degradation" + separator);
		pw.print("Revision" + separator);
		pw.print("Bug Fixing" + separator);
		pw.print("Number of Packages" + separator);
		pw.print("Number of Files" + separator);
		pw.print("Number of Issues" + separator);
		pw.print("Number of Insertions" + separator);
		pw.print("Number of Deletions" + separator);
		pw.print("Number of Churns" + separator);
		pw.print("Delta of Lines" + separator);
		pw.print("Number of Hunks" + separator);
		pw.print("Hour of Day" + separator);
		pw.println("Day of Week");
		
		for (String line : lines)
			pw.println(line);
		
		pw.close();
	}

}
