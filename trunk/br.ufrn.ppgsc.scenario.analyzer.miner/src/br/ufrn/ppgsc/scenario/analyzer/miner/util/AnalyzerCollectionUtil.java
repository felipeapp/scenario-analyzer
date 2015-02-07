package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerStatistical.StatElement;

public abstract class AnalyzerCollectionUtil {

	public static <T> Set<T> except(Collection<T> setA, Collection<T> setB) {
		Set<T> except = new HashSet<T>(setA);
		except.removeAll(setB);
		return except;
	}

	public static <T> Set<T> intersect(Collection<T> setA, Collection<T> setB) {
		Set<T> intersect = new HashSet<T>(setA);
		intersect.retainAll(setB);
		return intersect;
	}

	/** It uses the rate method to determine if there was performance degradation */
	public static List<StatElement> degradatedRate(Collection<StatElement> elements, double rate) {
		List<StatElement> result = new ArrayList<StatElement>();

		for (StatElement e : elements)
			if (e.getAVGv2() > e.getAVGv1() * (1 + rate))
				result.add(e);

		return result;
	}

	/** It uses the rate method to determine if there was performance optimization */
	public static List<StatElement> optimizedRate(Collection<StatElement> elements, double rate) {
		List<StatElement> result = new ArrayList<StatElement>();

		for (StatElement e : elements)
			if (e.getAVGv2() < e.getAVGv1() * (1 - rate))
				result.add(e);

		return result;
	}

	/** It uses the rate method to determine unchanged performance */
	public static List<StatElement> unchangedRate(Collection<StatElement> elements, double rate) {
		List<StatElement> result = new ArrayList<StatElement>();

		for (StatElement e : elements)
			if (e.getAVGv2() >= e.getAVGv1() * (1 - rate) && e.getAVGv2() <= e.getAVGv1() * (1 + rate))
				result.add(e);

		return result;
	}

	/** It uses the pvalue to determine performance degradation */
	public static List<StatElement> degradatedPValue(Collection<StatElement> elements, double alpha, AnalyzerStatistical.Tests test) {
		List<StatElement> result = new ArrayList<StatElement>();

		for (StatElement e : elements) {
			double pvalue;
			
			if (test == AnalyzerStatistical.Tests.TTest)
				pvalue = e.getTTestPvalue();
			else if (test == AnalyzerStatistical.Tests.UTest)
				pvalue = e.getUTestPvalue();
			else
				throw new RuntimeException("Target test invalid at runtime");
			
			if (pvalue <= alpha && e.getAVGv2() - e.getAVGv1() > 0)
				result.add(e);
		}

		return result;
	}
	
	/** It uses the pvalue to determine if there was performance optimization */
	public static List<StatElement> optimizedPValue(Collection<StatElement> elements, double alpha, AnalyzerStatistical.Tests test) {
		List<StatElement> result = new ArrayList<StatElement>();

		for (StatElement e : elements) {
			double pvalue;
			
			if (test == AnalyzerStatistical.Tests.TTest)
				pvalue = e.getTTestPvalue();
			else if (test == AnalyzerStatistical.Tests.UTest)
				pvalue = e.getUTestPvalue();
			else
				throw new RuntimeException("Target test invalid at runtime");
			
			if (pvalue <= alpha && e.getAVGv2() - e.getAVGv1() < 0)
				result.add(e);
		}

		return result;
	}
	
	/** It uses the pvalue to determine unchanged performance */
	public static List<StatElement> unchangedPValue(Collection<StatElement> elements, double alpha, AnalyzerStatistical.Tests test) {
		List<StatElement> result = new ArrayList<StatElement>();

		for (StatElement e : elements) {
			double pvalue;
			
			if (test == AnalyzerStatistical.Tests.TTest)
				pvalue = e.getTTestPvalue();
			else if (test == AnalyzerStatistical.Tests.UTest)
				pvalue = e.getUTestPvalue();
			else
				throw new RuntimeException("Target test invalid at runtime");
			
			if (pvalue > alpha || Double.isNaN(pvalue))
				result.add(e);
		}

		return result;
	}

}
