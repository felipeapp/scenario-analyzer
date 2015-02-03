package statistical;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;

import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDB;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDBHibernateImpl;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerCollectionUtil;

public class UTest {

	public static void main(String[] args) throws IOException {
		
		GenericDB dao_v1 = new GenericDBHibernateImpl("hibernate.cfg.1.xml");
		GenericDB dao_v2 = new GenericDBHibernateImpl("hibernate.cfg.2.xml");
		
		long time = System.currentTimeMillis();

		System.out.println("Collecting averages from release 1...");
		Map<String, Double> avgs_v1 = dao_v1.getExecutionTimeAverageOfMembers();
		System.out.println("\tTotal = " + avgs_v1.size());
		
		System.out.println("Collecting averages from release 2...");
		Map<String, Double> avgs_v2 = dao_v2.getExecutionTimeAverageOfMembers();
		System.out.println("\tTotal = " + avgs_v2.size());
		
		System.out.println("Collecting signatures from release 1...");
		List<String> signatures_v1 = dao_v1.getSignatureOfMembers();
		System.out.println("\tTotal = " + signatures_v1.size());
		
		System.out.println("Collecting signatures from release 2...");
		List<String> signatures_v2 = dao_v2.getSignatureOfMembers();
		System.out.println("\tTotal = " + signatures_v2.size());
		
		System.out.println("Intersection...");
		Set<String> signatures_commons = AnalyzerCollectionUtil.intersect(signatures_v1, signatures_v2);
		System.out.println("\tTotal = " + signatures_commons.size());
		
		for (String s : signatures_commons) {
			System.out.println("Collecting execution time from of " + s);
			
			long ti = System.currentTimeMillis();
			
			double[] execution_time_v1 = dao_v1.getAllExecutionTimeByMember(s);
			double[] execution_time_v2 = dao_v2.getAllExecutionTimeByMember(s);
			
			MannWhitneyUTest utest = new MannWhitneyUTest();
			double pvalue = utest.mannWhitneyUTest(execution_time_v1, execution_time_v2);
			
			double avg_v1 = avgs_v1.get(s);
			double avg_v2 = avgs_v2.get(s);
			double avg_var = 1 - Math.min(avg_v1, avg_v2) / Math.max(avg_v1, avg_v2);
			
			System.out.printf("\tTime = %.2f seconds\n", (System.currentTimeMillis() - ti) / 1000.0);
			System.out.printf("\tN1, N2 = %d, %d\n", execution_time_v1.length, execution_time_v2.length);
			System.out.printf("\tP-Value = %.5f\n", pvalue);
			System.out.printf("\tAVG-1 = %.2f\n", avg_v1);
			System.out.printf("\tAVG-2 = %.2f\n", avg_v2);
			System.out.printf("\tAVG-2 - AVG-1 = %.2f (%.2f%%)\n", avg_v2 - avg_v1, avg_var * 100);
			System.out.println("\tVariation (P-Value) = " + (pvalue <= 0.05));
			System.out.println("\tVariation (AVG) = " + (avg_var > 0.05));
		}
		

		System.out.println("Time: " + (System.currentTimeMillis() - time) / 1000.0);
		System.out.println("Size: " + signatures_commons.size());

	}

}
