package br.ufrn.ppgsc.scenario.analyzer.dataminer.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AnalyzerSetUtil {

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

	public static Set<String> degradated(Map<String, Double> mapA, Map<String, Double> mapB, double rate) {
		Set<String> commum = intersect(mapA.keySet(), mapB.keySet());
		Set<String> result = new HashSet<String>();

		for (String signature : commum)
			if (mapB.get(signature) > mapA.get(signature) * (1 + rate))
				result.add(signature);

		return result;
	}

	public static Set<String> optimized(Map<String, Double> mapA, Map<String, Double> mapB, double rate) {
		Set<String> commum = intersect(mapA.keySet(), mapB.keySet());
		Set<String> result = new HashSet<String>();

		for (String signature : commum)
			if (mapB.get(signature) < mapA.get(signature) * (1 - rate))
				result.add(signature);

		return result;
	}

	public static Set<String> unchanged(Map<String, Double> mapA, Map<String, Double> mapB, double rate) {
		Set<String> commum = intersect(mapA.keySet(), mapB.keySet());
		Set<String> result = new HashSet<String>();

		for (String signature : commum) {
			double avg1 = mapA.get(signature);
			double avg2 = mapB.get(signature);

			if (avg2 >= avg1 * (1 - rate) && avg2 <= avg1 * (1 + rate))
				result.add(signature);
		}

		return result;
	}

}
