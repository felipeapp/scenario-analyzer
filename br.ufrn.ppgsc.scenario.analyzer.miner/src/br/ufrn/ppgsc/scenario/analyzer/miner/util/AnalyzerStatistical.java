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
import br.ufrn.ppgsc.scenario.analyzer.miner.model.StatElement;

public class AnalyzerStatistical {

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

			// TODO: Descomentar depois
			if (target.equals(RuntimeScenario.class)) {
				execs_time_v1 = new double[1];//database_v1.getAllExecutionTimeByScenario(key);
				execs_time_v2 = new double[1];//database_v2.getAllExecutionTimeByScenario(key);
			} else if (target.equals(RuntimeNode.class)) {
				execs_time_v1 = new double[1];//database_v1.getAllExecutionTimeByMember(key);
				execs_time_v2 = new double[1];//database_v2.getAllExecutionTimeByMember(key);
			} else {
				throw new RuntimeException("Invalid target, aborting...");
			}

			if (execs_time_v1.length > 1 && execs_time_v2.length > 1) {
				utest_pvalue = new MannWhitneyUTest().mannWhitneyUTest(execs_time_v1, execs_time_v2);
				ttest_pvalue = TestUtils.tTest(execs_time_v1, execs_time_v2);
			}
			
			result.put(key, new StatElement(key, ttest_pvalue, utest_pvalue,
					avgs_v1.get(key), avgs_v2.get(key),
					execs_time_v1.length, execs_time_v2.length));
		}

		return result;
	}

}
