package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TestUtils;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeScenario;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.DatabaseRelease;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDB;

public class AnalyzerStatistical {

	public class StatElement {
		private String elementName;
		private double UTestPvalue;
		private double TTestPvalue;
		private double AVGv1;
		private double AVGv2;
		private int N1;
		private int N2;

		public StatElement(String elementName, double UTestPvalue, double TTestPvalue, double AVGv1, double AVGv2, int N1, int N2) {
			this.elementName = elementName;
			this.UTestPvalue = UTestPvalue;
			this.TTestPvalue = TTestPvalue;
			this.AVGv1 = AVGv1;
			this.AVGv2 = AVGv2;
			this.N1 = N1;
			this.N2 = N2;
		}

		public String getElementName() {
			return elementName;
		}

		public double getUTestPvalue() {
			return UTestPvalue;
		}

		public double getTTestPvalue() {
			return TTestPvalue;
		}

		public double getAVGv1() {
			return AVGv1;
		}

		public double getAVGv2() {
			return AVGv2;
		}

		public int getN1() {
			return N1;
		}

		public int getN2() {
			return N2;
		}

	}
	
	public enum Tests {TTest, UTest};

	public Map<String, StatElement> executeStatisticalTests(Map<String, Double> avgs_v1, Map<String, Double> avgs_v2, Class<?> target) {
		Map<String, StatElement> result = new HashMap<String, StatElement>();

		GenericDB database_v1 = DatabaseRelease.getDatabasev1();
		GenericDB database_v2 = DatabaseRelease.getDatabasev2();

		Set<String> commons = AnalyzerCollectionUtil.intersect(avgs_v1.keySet(), avgs_v2.keySet());
		
		int i = 0;
		for (String key : commons) {
			System.out.println(++i + " / " + commons.size() + " - Calculating statistical tests for " + key);

			double[] execs_time_v1 = null;
			double[] execs_time_v2 = null;

			double ttest_pvalue = 1;
			double utest_pvalue = 1;

			if (target.equals(RuntimeScenario.class)) {
				execs_time_v1 = database_v1.getAllExecutionTimeByScenario(key);
				execs_time_v2 = database_v2.getAllExecutionTimeByScenario(key);
			} else if (target.equals(RuntimeNode.class)) {
				execs_time_v1 = database_v1.getAllExecutionTimeByMember(key);
				execs_time_v2 = database_v2.getAllExecutionTimeByMember(key);
			} else {
				throw new RuntimeException("Invalid target, aborting...");
			}

			if (execs_time_v1.length > 1 && execs_time_v2.length > 1) {
				utest_pvalue = new MannWhitneyUTest().mannWhitneyUTest(execs_time_v1, execs_time_v2);
				ttest_pvalue = TestUtils.tTest(execs_time_v1, execs_time_v2);
			}
			
			result.put(key, new StatElement(key, utest_pvalue, ttest_pvalue,
					avgs_v1.get(key), avgs_v2.get(key),
					execs_time_v1.length, execs_time_v2.length));
		}

		return result;
	}

}
