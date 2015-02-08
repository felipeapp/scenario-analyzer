package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeGenericAnnotation;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.DatabaseRelease;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDB;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.StatElement;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedMethod;

public abstract class AnalyzerReportUtil {

	public static void saveReport(
			String message, String filename,
			Map<String, Double> avgs, Collection<String> elements,
			double rate, double alpha) throws FileNotFoundException {
		
		System.out.println("Saving >> " + message);

		PrintWriter pw = new PrintWriter(new FileOutputStream(filename));

		pw.println(message);
		pw.println(elements.size());
		pw.println("Name;Average;N");

		for (String key : elements)
			pw.println(key + ";" + avgs.get(key));
		
		pw.println(rate);
		pw.println(alpha);
		
		pw.close();
		
	}
	
	public static void saveReport(
			String message, String filename, Collection<StatElement> collection,
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
	
	public static void saveReport(String message, String filename,
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
				pw.println((double) failed_methods.get(sig) / db.getNumberOfMethodExecution(sig));
				
				pw.println(n.getAnnotations().size());
				
				for (RuntimeGenericAnnotation annotation : n.getAnnotations())
					pw.println(annotation.getName());
			}
			
			pw.println("----------------------------------------------------------------------");
		}
		
		pw.close();
		
	}
	
	public static void saveReport(String message, String filename, Collection<String> collection) throws FileNotFoundException {
		System.out.println("Saving >> " + message);
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(filename));
		
		pw.println(message);
		pw.println(collection.size());
		
		for (String elem : collection)
			pw.println(elem);
		
		pw.close();
	}
	
	public static void saveReport(String message, String filename,
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
					Collection<Issue> issue_collection = upline.getIssues();
				
					pw.println("\t\tLine Number: " + upline.getLineNumber());
					pw.println("\t\tCommit: " + upline.getRevision());
					pw.println("\t\t\tNumber of Issues: " + issue_collection.size());
					
					for (Issue issue : issue_collection)
						if (issue.getNumber() > 0)
							pw.println("\t\t\t" + issue.getIssueId() + ";" + issue.getNumber() + ";" + issue.getIssueType());
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
	
	public static void getImpactedElementsAndsaveReport(String message, String filename, Collection<String> members) throws FileNotFoundException {
		GenericDB database_v2 = DatabaseRelease.getDatabasev2();
		
		System.out.println("persistFile: " + message);
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(filename));
		
		pw.println(message);
		pw.println(members.size());
		
		int i = 0;
		for (String sig : members) {
			System.out.println("[" + ++i + "/" + members.size() + "] Retrieving impacted members and scenarios by " + sig);
			
//			Set<String> nodes = database_v2.getImpactedNodes(sig);
			List<String> scenarios = database_v2.getScenariosByMember(sig);
			
//			pw.println(sig + ":" + nodes.size());
			pw.println(sig);
			pw.println(scenarios.size());
			
			for (String s : scenarios)
				pw.println(s);
		}
		
		pw.close();
	}
	
	public static void saveReport(String message, String filename, Map<String, Collection<UpdatedMethod>> signature_upmethod) throws FileNotFoundException {
		System.out.println("persistFile: " + message);
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(filename));
		
		pw.println(message);
		pw.println(signature_upmethod.size());
		
		for (String signature : signature_upmethod.keySet()) {
			pw.println(signature);
			
			Collection<UpdatedMethod> upmethod_list = signature_upmethod.get(signature);
			
			pw.println("Members " + upmethod_list.size());
			
			for (UpdatedMethod method : upmethod_list) {
				List<UpdatedLine> upl_list = method.getUpdatedLines();
				Set<String> counte_revisions = new HashSet<String>();
				
				pw.print(method.getMethodLimit().getSignature() + " ");
				pw.print(method.getMethodLimit().getStartLine() + " ");
				pw.println(method.getMethodLimit().getEndLine());
				pw.println("Lines " + upl_list.size());
				
				for (UpdatedLine up_line : upl_list) {
					if (counte_revisions.contains(up_line.getRevision()))
						continue;
					
					List<Issue> issues = up_line.getIssues();
				
					pw.println("Revision " + up_line.getRevision());
					
					if (issues.size() == 1 && issues.get(0).getNumber() < 0)
						pw.println("Issues " + 0);
					else
						pw.println("Issues " + issues.size());
					
					counte_revisions.add(up_line.getRevision());
					
					for (Issue issue : issues) {
						if (issue.getNumber() >= 0) {
							pw.print("IssueID " + issue.getIssueId() + " | ");
							pw.print("IssueNumber " + issue.getNumber() + " | ");
							pw.print("IssueType " + issue.getIssueType() + " | ");
							pw.println("ShortDescription " + issue.getShortDescription());
						}
					}
				}
			}
		}
		
		Map<String, Integer> counter_task_types = AnalyzerCollectionUtil.counterTaskTypes(signature_upmethod);
		Map<String, Collection<UpdatedMethod>> task_members = AnalyzerCollectionUtil.getTaskMembers(signature_upmethod);
		
		pw.println("TotalOfTypes " + counter_task_types.size());
		
		for (String type : counter_task_types.keySet())
			pw.println(type + " " + counter_task_types.get(type));
		
		pw.println("TotalOfTypes " + task_members.size());

		for (String type : task_members.keySet()) {
			Collection<UpdatedMethod> list = task_members.get(type);

			pw.println(type);
			pw.println("TotalOfMembers " + list.size());
			for (UpdatedMethod up : list)
				pw.println(up.getMethodLimit().getSignature());
		}
		
		pw.close();
	}
	
	public static String loadReport(Collection<String> map, String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		
		String message = br.readLine();
		System.out.println("Loading >> " + message);
		
		int number_of_registers = Integer.parseInt(br.readLine());
		
		/*
		 * Just read the header
		 * Name;P-Value (TTest);P-Value (UTest);Mean R1;Mean R2;N1;N2
		 * 0    1               2               3       4       5  6
		 */
		br.readLine();
		
		for (int i = 0; i < number_of_registers; i++) {
			String[] line = br.readLine().split(";");
			map.add(line[0]);
		}
		
		/* 
		 * This method should read the performance rate and the significance level here.
		 * However I do not need them at this moment. So, I will just close the reader.
		 */
		br.close();
		
		System.out.println("\tTotal = " + map.size());
		
		return message;
	}

}
