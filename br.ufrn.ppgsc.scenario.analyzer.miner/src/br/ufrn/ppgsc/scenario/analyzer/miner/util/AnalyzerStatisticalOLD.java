package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TestUtils;

import br.ufrn.ppgsc.scenario.analyzer.miner.db.DatabaseRelease;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDB;

public class AnalyzerStatisticalOLD {
	
	public class StatElement {
		private double uTestPvalue;
		private double tTestPvalue;
		private double AVGv1;
		private double AVGv2;

		public double getuTestPvalue() {
			return uTestPvalue;
		}

		public void setuTestPvalue(double uTestPvalue) {
			this.uTestPvalue = uTestPvalue;
		}

		public double gettTestPvalue() {
			return tTestPvalue;
		}

		public void settTestPvalue(double tTestPvalue) {
			this.tTestPvalue = tTestPvalue;
		}

		public double getAVGv1() {
			return AVGv1;
		}

		public void setAVGv1(double aVGv1) {
			AVGv1 = aVGv1;
		}

		public double getAVGv2() {
			return AVGv2;
		}

		public void setAVGv2(double aVGv2) {
			AVGv2 = aVGv2;
		}

	}
	
	public static enum Statistical {TTest, UTest};
	public static enum Target {Scenario, Member};
	
	// Keys (Scenario name or method name) and pvalues for UTest
	private Map<String, Double> u_degraded;
	private Map<String, Double> u_optimized;
	private Map<String, Double> u_unchanged;
	
	// Keys (Scenario name or method name) and pvalues for TTest
	private Map<String, Double> t_degraded;
	private Map<String, Double> t_optimized;
	private Map<String, Double> t_unchanged;
	
	public AnalyzerStatisticalOLD() {
		u_degraded = new HashMap<String, Double>();
		u_optimized = new HashMap<String, Double>();
		u_unchanged = new HashMap<String, Double>();
		
		t_degraded = new HashMap<String, Double>();
		t_optimized = new HashMap<String, Double>();
		t_unchanged = new HashMap<String, Double>();
	}
	
	public void executeStatisticalTest(Map<String, Double> averages_v1, Map<String, Double> averages_v2, Target target, double significance_level) {
		GenericDB database_v1 = DatabaseRelease.getDatabasev1();
		GenericDB database_v2 = DatabaseRelease.getDatabasev2();
		
		Set<String> commum = AnalyzerCollectionUtil.intersect(averages_v1.keySet(), averages_v2.keySet());
		
		for (String key : commum) {
			double[] execs_time_v1 = null;
			double[] execs_time_v2 = null;
			
			double ttest_pvalue = 1;
			double utest_pvalue = 1;
			
			if (target == Target.Scenario) {
				execs_time_v1 = database_v1.getAllExecutionTimeByScenario(key);
				execs_time_v2 = database_v2.getAllExecutionTimeByScenario(key);
			}
			else if (target == Target.Member) {
				execs_time_v1 = database_v1.getAllExecutionTimeByMember(key);
				execs_time_v2 = database_v2.getAllExecutionTimeByMember(key);
			}
			else {
				System.err.println("Invalid target, aborting...");
				System.exit(0);
			}
			
			if (execs_time_v1.length > 1 && execs_time_v2.length > 1) {
				utest_pvalue = new MannWhitneyUTest().mannWhitneyUTest(execs_time_v1, execs_time_v2);
				ttest_pvalue = TestUtils.tTest(execs_time_v1, execs_time_v2);
			}
			
			savePValue(averages_v1, averages_v2, u_degraded, u_optimized, u_unchanged, key, utest_pvalue, significance_level);
			savePValue(averages_v1, averages_v2, t_degraded, t_optimized, t_unchanged, key, ttest_pvalue, significance_level);
		}
	}
	
	private void savePValue(
			Map<String, Double> averages_v1, Map<String, Double> averages_v2,
			Map<String, Double> degraded, Map<String, Double> optimized, Map<String, Double> unchanged,
			String key, double pvalue, double significance_level) {
		
		if (pvalue <= significance_level) {
			double avg_v1 = averages_v1.get(key);
			double avg_v2 = averages_v2.get(key);
			
			if (avg_v2 - avg_v1 > 0) {
				degraded.put(key, pvalue);
			}
			else if (avg_v2 - avg_v1 < 0) {
				optimized.put(key, pvalue);
			}
			else {
				System.err.println("The means should not be the same for pvalue <= 0.05, aborting");
				System.exit(0);					
			}
		}
		else {
			unchanged.put(key, pvalue);
		}
		
	}
	
	public Map<String, Double> getDegraded(Statistical test) {
		return (test == Statistical.UTest)? u_degraded : t_degraded;
	}
	
	public Map<String, Double> getOptimized(Statistical test) {
		return (test == Statistical.UTest)? u_optimized : t_optimized;
	}
	
	public Map<String, Double> getUnchanged(Statistical test) {
		return (test == Statistical.UTest)? u_unchanged : t_unchanged;
	}

}
