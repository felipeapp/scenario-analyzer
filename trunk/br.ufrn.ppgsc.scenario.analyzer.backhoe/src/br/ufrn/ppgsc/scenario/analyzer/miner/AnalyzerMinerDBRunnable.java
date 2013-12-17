package br.ufrn.ppgsc.scenario.analyzer.miner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeGenericAnnotation;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.d.util.QueryRuntimeUtil;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.DatabaseService;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDB;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerCollectionUtil;

public final class AnalyzerMinerDBRunnable {

	private GenericDB database_v1;
	private GenericDB database_v2;
	
	private double performance_rate;
	private String system_id;
	
	private String strdate;
	
	public AnalyzerMinerDBRunnable(Properties properties) throws IOException {
		system_id = properties.getProperty("system_id");
		performance_rate = Double.parseDouble(properties.getProperty("performance_rate"));
		
		database_v1 = new DatabaseService(properties.getProperty("database_v1")).getGenericDB();
		database_v2 = new DatabaseService(properties.getProperty("database_v2")).getGenericDB();
		
		strdate = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date());
	}
	
	private void persistFile(String message, String partial_name, Collection<String> collection, double rate) throws FileNotFoundException {
		System.out.println("persistFile: " + message);
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(
				"log/" + system_id + "_" + partial_name + "_" + strdate + ".log", true));
		
		pw.println(message);
		
		pw.println(collection.size());
		
		for (String elem : collection)
			pw.println(elem);
		
		if (rate > 0)
			pw.println(rate);
		
		pw.close();
	}
	
	private void persistFile(String message, String partial_name,
			Map<RuntimeScenario, List<RuntimeNode>> map_scenario_node,
			Map<String, Integer> map_failed_methods, GenericDB db) throws FileNotFoundException {
		System.out.println("persistFile: " + message);
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(
				"log/" + system_id + "_" + partial_name + "_" + strdate + ".log", true));
		
		pw.println(message);
		pw.println(map_scenario_node.size());
		
		for (RuntimeScenario s : map_scenario_node.keySet()) {
			List<RuntimeNode> nodes = map_scenario_node.get(s);
			
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
				pw.println((double) map_failed_methods.get(sig) / db.getNumberOfMethodExecution(sig));
				
				pw.println(n.getAnnotations().size());
				
				for (RuntimeGenericAnnotation annotation : n.getAnnotations())
					pw.println(annotation.getName());
			}
			
			pw.println("-----------------------------------");
		}
		
		pw.close();
	}
	
	private Map<RuntimeScenario, List<RuntimeNode>> buildMapOfFailedScenarios(
			Collection<RuntimeScenario> failed_scenarios, Map<String, Integer> node_signatures) {
		int i = 0;
		Map<RuntimeScenario, List<RuntimeNode>> map = new HashMap<RuntimeScenario, List<RuntimeNode>>();
		
		for (RuntimeScenario s : failed_scenarios) {
			long start = System.currentTimeMillis();
			
			System.out.print("Building map for scenario " + s.getId() + ", " + s.getName() + ", " + ++i + "/" + failed_scenarios.size());
			
			List<RuntimeNode> failed_nodes = QueryRuntimeUtil.getFailedNodes(s);
			
			map.put(s, failed_nodes);
			
			for (RuntimeNode n : failed_nodes) {
				Integer count = node_signatures.get(n.getMemberSignature());
				
				if (count == null)
					count = 0;
				
				node_signatures.put(n.getMemberSignature(), count + 1);
			}
			
			System.out.println(", " + (int)((System.currentTimeMillis() - start) / 1000.0 / 60.0) + " minutes");
		}
		
		return map;
	}
	
	public String run() throws FileNotFoundException {
		Map<String, Double> avg_time_v1 = database_v1.getExecutionTimeAverage();
		Map<String, Double> avg_time_v2 = database_v2.getExecutionTimeAverage();

		Set<String> excluded_methods = AnalyzerCollectionUtil.except(avg_time_v1.keySet(), avg_time_v2.keySet());
		Set<String> changed_methods = AnalyzerCollectionUtil.except(avg_time_v2.keySet(), avg_time_v1.keySet());
		Set<String> kept_methods = AnalyzerCollectionUtil.intersect(avg_time_v1.keySet(), avg_time_v2.keySet());
		
		Collection<String> p_degradated_methods = AnalyzerCollectionUtil.degradated(avg_time_v1, avg_time_v2, performance_rate);
		Collection<String> p_optimized_methods = AnalyzerCollectionUtil.optimized(avg_time_v1, avg_time_v2, performance_rate);
		Collection<String> p_unchanged_methods = AnalyzerCollectionUtil.unchanged(avg_time_v1, avg_time_v2, performance_rate);
		
		persistFile("# Métodos executados na primeira versão, mas não na evolução", "excluded_methods", excluded_methods, 0);
		persistFile("# Métodos executados na evolução, mas não na primeira versão", "changed_methods", changed_methods, 0);
		persistFile("# Métodos que foram executados nas duas versões", "kept_methods", kept_methods, 0);
		
		persistFile("# Métodos que tiveram performance degradada na evolução", "p_degradated_methods", p_degradated_methods, performance_rate);
		persistFile("# Métodos que tiveram performance otimizada na evolução", "p_optimized_methods", p_optimized_methods, performance_rate);
		persistFile("# Métodos que tiveram performance inalterada na evolução", "p_unchanged_methods", p_unchanged_methods, performance_rate);
		
		Collection<RuntimeScenario> failed_scenarios_v1 = database_v1.getScenariosFailed();
		Collection<RuntimeScenario> failed_scenarios_v2 = database_v2.getScenariosFailed();
		
		Map<String, Integer> failed_methods_v1 = new HashMap<String, Integer>();
		Map<String, Integer> failed_methods_v2 = new HashMap<String, Integer>();
		
		Map<RuntimeScenario, List<RuntimeNode>> map_failed_scenario_node_v1 = buildMapOfFailedScenarios(failed_scenarios_v1, failed_methods_v1);
		persistFile("# Cenários que falharam na primeira versão", "failed_scenarios_v1", map_failed_scenario_node_v1, failed_methods_v1, database_v1);
		
		Map<RuntimeScenario, List<RuntimeNode>> map_failed_scenario_node_v2 = buildMapOfFailedScenarios(failed_scenarios_v2, failed_methods_v2);
		persistFile("# Cenários que falharam na evolução", "failed_scenarios_v2", map_failed_scenario_node_v2, failed_methods_v2, database_v2);
		
		Set<String> failed_methods_only_v1 = AnalyzerCollectionUtil.except(failed_methods_v1.keySet(), failed_methods_v2.keySet());
		Set<String> failed_methods_only_v2 = AnalyzerCollectionUtil.except(failed_methods_v2.keySet(), failed_methods_v1.keySet());
		Set<String> failed_methods_both = AnalyzerCollectionUtil.intersect(failed_methods_v1.keySet(), failed_methods_v2.keySet());
		
		persistFile("# Métodos que falharam apenas na primeira versão", "failed_methods_only_v1", failed_methods_only_v1, 0);
		persistFile("# Métodos que falharam apenas na evolução", "failed_methods_only_v2", failed_methods_only_v2, 0);
		persistFile("# Métodos que falharam em ambas as versões", "failed_methods_both", failed_methods_both, 0);
		
		return strdate;
	}

}
