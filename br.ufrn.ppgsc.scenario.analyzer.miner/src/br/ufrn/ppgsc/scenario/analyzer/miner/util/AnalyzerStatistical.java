package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.miner.model.DoubleStatElement;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.SimpleStatElement;

public class AnalyzerStatistical {

	public enum Tests {
		TTest, UTest, Rate
	};

	public Map<String, DoubleStatElement> executeStatisticalTests(Set<String> set_of_keys,
			Map<String, SimpleStatElement> stat_v1, Map<String, SimpleStatElement> stat_v2) {
		Map<String, DoubleStatElement> result = new HashMap<String, DoubleStatElement>();

		for (String key : set_of_keys) {
			double[] execs_time_v1 = stat_v1.get(key).getTimeMeasurements();
			double[] execs_time_v2 = stat_v2.get(key).getTimeMeasurements();

			double ttest_pvalue = 1;
			double utest_pvalue = 1;

			if (execs_time_v1.length > 1 && execs_time_v2.length > 1) {
				utest_pvalue = StatisticsUtil.mannWhitneyUTest(execs_time_v1, execs_time_v2);
				ttest_pvalue = StatisticsUtil.tTest(execs_time_v1, execs_time_v2);
			}

			result.put(key, new DoubleStatElement(key, ttest_pvalue, utest_pvalue, execs_time_v1, execs_time_v2));
		}

		return result;
	}

}
