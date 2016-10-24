package br.ufrn.ppgsc.scenario.analyzer.miner.util;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;
import org.apache.commons.math3.stat.inference.TestUtils;
import org.apache.commons.math3.stat.inference.WilcoxonSignedRankTest;

public class StatisticsUtil {

	/**
	 * This uses apache libs to calculate the observed significance level, or
	 * p-value, associated with the Student's T-test.
	 * 
	 * @param sample1
	 *            Values for sample one
	 * @param sample2
	 *            Values for sample two
	 * @return The p-value associated with the Student's T-test
	 */
	public static double tTest(double[] sample1, double[] sample2) {
		return TestUtils.tTest(sample1, sample2);
	}

	/**
	 * This uses apache libs to calculate the asymptotic observed significance
	 * level, or p-value, associated with a Mann-Whitney U-Test, also called
	 * Wilcoxon rank-sum test.
	 * 
	 * @param sample1
	 *            Values for sample one
	 * @param sample2
	 *            Values for sample two
	 * @return The p-value associated with a Mann-Whitney U-Test
	 */
	public static double mannWhitneyUTest(double[] sample1, double[] sample2) {
		return new MannWhitneyUTest().mannWhitneyUTest(sample1, sample2);
	}

	/**
	 * This uses apache libs to calculate the observed significance level, or
	 * p-value, associated with the Wilcoxon signed ranked test.
	 * 
	 * @param sample1
	 *            Values for sample one
	 * @param sample2
	 *            Values for sample two
	 * @return The p-value associated with the Wilcoxon signed ranked test
	 */
	public static double wilcoxonSignedRankTest(double[] sample1, double[] sample2) {
		return new WilcoxonSignedRankTest().wilcoxonSignedRankTest(sample1, sample2, true);
	}

	/**
	 * This uses apache libs to calculate the arithmetic mean of the entries in
	 * the input array.
	 * 
	 * @param values
	 *            Entries
	 * @return The arithmetic mean of the entries
	 */
	public static double mean(double[] values) {
		return StatUtils.mean(values);
	}

	/**
	 * This uses apache libs to calculate the arithmetic median of the entries
	 * in the input array.
	 * 
	 * @param values
	 *            Entries
	 * @return The arithmetic median of the entries
	 */
	public static double median(double[] values) {
		DescriptiveStatistics d = new DescriptiveStatistics(values);
		return d.getPercentile(50);
	}

	/**
	 * This uses apache libs to calculate the signed mean difference between two
	 * samples.
	 * 
	 * @param sample1
	 *            Values for sample one
	 * @param sample2
	 *            Values for sample two
	 * @param paired
	 *            It should be true if the samples are paired
	 * @return The signed mean difference between the samples
	 */
	public static double meanDifference(double[] sample1, double[] sample2, boolean paired) {
		return paired ? StatUtils.meanDifference(sample1, sample2) : StatUtils.mean(sample1) - StatUtils.mean(sample2);
	}

	/**
	 * It calculates the coefficient of variation (CV), also known as relative
	 * standard deviation (RSD). CV is defined as the ratio of the standard
	 * deviation to the mean. You should not use this method when the mean of
	 * the input array was already calculated. It will recalculate the mean
	 * again.
	 * 
	 * @param values
	 *            The input array (population)
	 * @return The coefficient of variation (CV)
	 */
	public static double coefficientOfVariation(double[] values) {
		return coefficientOfVariation(values, StatUtils.mean(values));
	}

	/**
	 * It calculates the coefficient of variation (CV), also known as relative
	 * standard deviation (RSD). CV is defined as the ratio of the standard
	 * deviation to the mean. You should use this method when the mean of the
	 * input array was already calculated.
	 * 
	 * @param values
	 *            The input array (population)
	 * @param mean
	 *            The mean of the input array values
	 * @return The coefficient of variation (CV)
	 */
	public static double coefficientOfVariation(double[] values, double mean) {
		return new StandardDeviation().evaluate(values, mean) / mean;
	}

}