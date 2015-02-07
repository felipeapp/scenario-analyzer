package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeGenericAnnotation;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.DatabaseRelease;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDB;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerStatistical.StatElement;

public abstract class AnalyzerReportUtil {

	public static void saveReport(
			String message, String filename,
			Map<String, Double> avgs, Collection<String> elements,
			double rate, double alpha) throws FileNotFoundException {
		
		System.out.println("Persisting >> " + message);

		PrintWriter pw = new PrintWriter(new FileOutputStream(filename, true));

		pw.println(message);
		pw.println(elements.size());
		
		pw.println("Name;Average");

		for (String key : elements) {
			pw.printf("%s;%f\n", key, avgs.get(key));
		}
		
		pw.close();
		
	}
	
	public static void saveReport(
			String message, String filename, Collection<StatElement> collection,
			double rate, double alpha) throws FileNotFoundException {
		
		System.out.println("Persisting >> " + message);

		PrintWriter pw = new PrintWriter(new FileOutputStream(filename, true));

		pw.println(message);
		pw.println(collection.size());
		
		pw.println("Name;P-Value (TTest);P-Value (UTest);Mean R1;Mean R2;N1;N2");

		for (StatElement e : collection) {
			pw.printf("%s;%f;%f;%f;%f;%d;%d\n", e.getElementName(), e.getTTestPvalue(),
					e.getUTestPvalue(), e.getAVGv1(), e.getAVGv2(),
					e.getN1(), e.getN2());
		}

		pw.println(rate);
		pw.println(alpha);
		
		pw.close();
		
	}
	
	public static void saveReport(String message, String filename,
			Map<RuntimeScenario, List<RuntimeNode>> scenario_to_nodes,
			Map<String, Integer> failed_methods, int release) throws FileNotFoundException {
		
		System.out.println("Persisting >> " + message);
		
		GenericDB db;
		
		if (release == 1)
			db = DatabaseRelease.getDatabasev1();
		else if (release == 2)
			db = DatabaseRelease.getDatabasev2();
		else
			throw new RuntimeException("Wrong release at runtime, aborting...");
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(filename), true);
		
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
		System.out.println("persistFile: " + message);
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(filename, true));
		
		pw.println(message);
		pw.println(collection.size());
		
		for (String elem : collection)
			pw.println(elem);
		
		pw.close();
	}	

}
