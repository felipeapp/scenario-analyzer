package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TestUtils;

import br.ufrn.ppgsc.scenario.analyzer.miner.db.DatabaseRelease;
import br.ufrn.ppgsc.scenario.analyzer.miner.db.GenericDB;

public class AnalyzerStatistical {
	
	private Set<String> degraded;
	private Set<String> optimized;
	private Set<String> unchanged;
	
	public static enum Statistical {TTest, UTest};
	public static enum Target {Scenario, Member};
	
	public AnalyzerStatistical() {
		degraded = new HashSet<String>();
		optimized = new HashSet<String>();
		unchanged = new HashSet<String>();
	}
	
	public void executeStatisticalTest(Map<String, Double> mapA, Map<String, Double> mapB, Target target, Statistical test) {
		Set<String> commum = AnalyzerCollectionUtil.intersect(mapA.keySet(), mapB.keySet());

		GenericDB database_v1 = DatabaseRelease.getDatabasev1();
		GenericDB database_v2 = DatabaseRelease.getDatabasev2();
		
		for (String key : commum) {
			double[] execs_time_v1 = null;
			double[] execs_time_v2 = null;
			double pvalue = 1;
			
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
			
			if (test == Statistical.UTest) {
				if (execs_time_v1.length > 1 && execs_time_v2.length > 1)
					pvalue = new MannWhitneyUTest().mannWhitneyUTest(execs_time_v1, execs_time_v2);
			}
			else if (test == Statistical.TTest) {
				if (execs_time_v1.length > 1 && execs_time_v2.length > 1)
					pvalue = TestUtils.tTest(execs_time_v1, execs_time_v2);
			}
			else {
				System.err.println("Invalid test, aborting...");
				System.exit(0);
			}
			
			if (pvalue <= 0.05) {
				double avg_v1 = mapA.get(key);
				double avg_v2 = mapB.get(key);
				
				if (avg_v2 - avg_v1 > 0)
					degraded.add(key);
				else if (avg_v2 - avg_v1 < 0) {
					optimized.add(key);
				}
				else {
					System.err.println("The means should not be the same for pvalue <= 0.05, aborting");
					System.exit(0);					
				}
			}
			else {
				unchanged.add(key);
			}
		}
	}

}
