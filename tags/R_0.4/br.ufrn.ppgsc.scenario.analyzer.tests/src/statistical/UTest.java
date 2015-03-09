package statistical;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TestUtils;

import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDB;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDBHibernateImpl;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerCollectionUtil;

public class UTest {

	public static void main(String[] args) throws IOException {
		
		String[] hibernate_cfgs = {"hibernate.cfg.1.xml", "hibernate.cfg.2.xml"};
		
		for (int i = 0; i < hibernate_cfgs.length - 1; i++) {
			GenericDB dao_v1 = new GenericDBHibernateImpl(hibernate_cfgs[i]);
			GenericDB dao_v2 = new GenericDBHibernateImpl(hibernate_cfgs[i + 1]);
			
			long time = System.currentTimeMillis();
	
			System.out.println("Collecting averages from release 1...");
			Map<String, Double> avgs_v1 = dao_v1.getExecutionTimeAverageOfMembers();
			System.out.println("\tTotal = " + avgs_v1.size());
			
			System.out.println("Collecting averages from release 2...");
			Map<String, Double> avgs_v2 = dao_v2.getExecutionTimeAverageOfMembers();
			System.out.println("\tTotal = " + avgs_v2.size());
			
			System.out.println("Intersection...");
			Set<String> signatures_commons = AnalyzerCollectionUtil.intersect(avgs_v1.keySet(), avgs_v2.keySet());
			System.out.println("\tTotal = " + signatures_commons.size());
			
			int j = 0;
			for (String s : signatures_commons) {
				System.out.println(++j + " - Collecting execution time from of " + s);
				
				long ti = System.currentTimeMillis();
				
				double[] execution_time_v1 = dao_v1.getAllExecutionTimeByMember(s);
				double[] execution_time_v2 = dao_v2.getAllExecutionTimeByMember(s);
				
				double pvalue_u = 1;
				double pvalue_t = 1;
				
				if (execution_time_v1.length > 1 && execution_time_v2.length > 1) {
					pvalue_u = new MannWhitneyUTest().mannWhitneyUTest(execution_time_v1, execution_time_v2);
					pvalue_t = TestUtils.tTest(execution_time_v1, execution_time_v2);
				}
				
				double avg_v1 = avgs_v1.get(s);
				double avg_v2 = avgs_v2.get(s);
				double avg_var = 1 - Math.min(avg_v1, avg_v2) / Math.max(avg_v1, avg_v2);
				
				System.out.printf("\tTime = %.2f seconds\n", (System.currentTimeMillis() - ti) / 1000.0);
				System.out.printf("\tN1, N2 = %d, %d\n", execution_time_v1.length, execution_time_v2.length);
				System.out.printf("\tP-Value (UTest) = %.5f\n", pvalue_u);
				System.out.printf("\tP-Value (TTest) = %.5f\n", pvalue_t);
				System.out.printf("\tAVG-1 = %.2f\n", avg_v1);
				System.out.printf("\tAVG-2 = %.2f\n", avg_v2);
				System.out.printf("\tAVG-2 - AVG-1 = %.2f (%.2f%%)\n", avg_v2 - avg_v1, avg_var * 100);
				System.out.println("\tVariation (P-Value-U) = " + (pvalue_u <= 0.05));
				System.out.println("\tVariation (P-Value-T) = " + (pvalue_t <= 0.05));
				System.out.println("\tVariation (AVG) = " + (avg_var > 0.05));
			}
	
			System.out.println("Time: " + (System.currentTimeMillis() - time) / 1000.0);
			System.out.println("Size: " + signatures_commons.size());
		}

	}

}
