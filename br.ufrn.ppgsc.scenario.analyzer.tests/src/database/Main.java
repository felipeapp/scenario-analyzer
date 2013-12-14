package database;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.d.data.Execution;
import br.ufrn.ppgsc.scenario.analyzer.dataminer.DatabaseService;
import br.ufrn.ppgsc.scenario.analyzer.dataminer.GenericDAO;
import br.ufrn.ppgsc.scenario.analyzer.dataminer.util.AnalyzerSetUtil;

public class Main {

	public static void main(String[] args) throws IOException {

		GenericDAO<Execution> daov1 = new DatabaseService<Execution>("executions_sigaa_3.11.24_hibernate.cfg.xml").getGenericDAO();
		GenericDAO<Execution> daov2 = new DatabaseService<Execution>("executions_sigaa_3.12.18_hibernate.cfg.xml").getGenericDAO();

		Map<String, Double> avgsv1 = daov1.getExecutionTimeAverage();
		Map<String, Double> avgsv2 = daov2.getExecutionTimeAverage();

		Set<String> changed_methods = AnalyzerSetUtil.except(avgsv2.keySet(), avgsv1.keySet());
		Set<String> kept_methods = AnalyzerSetUtil.intersect(avgsv1.keySet(), avgsv2.keySet());
		
		Set<String> degradated_methods = AnalyzerSetUtil.degradated(avgsv1, avgsv2, 0.05);
		Set<String> optimized_methods = AnalyzerSetUtil.optimized(avgsv1, avgsv2, 0.05);
		Set<String> unchanged_methods = AnalyzerSetUtil.unchanged(avgsv1, avgsv2, 0.05);
		
		System.out.println("\nNovos métodos executados na evolução: " + changed_methods.size());
		System.out.println("Métodos que foram mantidos na evolução: " + kept_methods.size());
		
		System.out.println("Métodos que foram degradados na evolução: " + degradated_methods.size());
		System.out.println("Métodos que foram melhorados na evolução: " + optimized_methods.size());
		System.out.println("Métodos inalterados na evolução: " + unchanged_methods.size());

	}

}
