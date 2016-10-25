package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TestUtils;

import br.ufrn.ppgsc.scenario.analyzer.miner.model.DoubleStatElement;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.SimpleStatElement;

public class AnalyzerStatistical {

	public enum Tests {
		TTest, UTest, Rate
	};

	public Map<String, DoubleStatElement> executeStatisticalTests(Set<String> set_of_keys,
			Map<String, SimpleStatElement> stat_v1, Map<String, SimpleStatElement> stat_v2) {
		Map<String, DoubleStatElement> result = new HashMap<String, DoubleStatElement>();

		int i = 0;
		for (String key : set_of_keys) {
			System.out.println(++i + " / " + set_of_keys.size() + " - Calculating statistical tests for " + key);

			double[] execs_time_v1 = stat_v1.get(key).getTimeMeasurements();
			double[] execs_time_v2 = stat_v2.get(key).getTimeMeasurements();

			double ttest_pvalue = 1;
			double utest_pvalue = 1;

			if (execs_time_v1.length > 1 && execs_time_v2.length > 1) {
				utest_pvalue = new MannWhitneyUTest().mannWhitneyUTest(execs_time_v1, execs_time_v2);
				ttest_pvalue = TestUtils.tTest(execs_time_v1, execs_time_v2);
			}

			result.put(key, new DoubleStatElement(key, ttest_pvalue, utest_pvalue, execs_time_v1, execs_time_v2));
		}

		return result;
	}

	public static void main(String[] args) {
		double[] execs_time_v1 = { 100, 101, 102, 99, 100 };
		double[] execs_time_v2 = { 110, 107, 101, 105, 110 };

		double utest_pvalue = new MannWhitneyUTest().mannWhitneyUTest(execs_time_v1, execs_time_v2);

		System.out.println("P-Value: " + utest_pvalue);
		System.out.println("AVG 1: " + new DescriptiveStatistics(execs_time_v1).getMean());
		System.out.println("AVG 2: " + new DescriptiveStatistics(execs_time_v2).getMean());

	}

}
