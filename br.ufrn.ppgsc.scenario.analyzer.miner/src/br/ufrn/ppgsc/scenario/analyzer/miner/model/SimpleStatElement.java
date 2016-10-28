package br.ufrn.ppgsc.scenario.analyzer.miner.model;

import br.ufrn.ppgsc.scenario.analyzer.miner.util.StatisticsUtil;

public class SimpleStatElement extends AbstractStatElement {

	public static final String HEADER = "Name;Average;N";

	// Average execution time
	private double average;

	// Every time of the element
	private double[] timeMeasurements;

	// How many times the element was executed
	private int numberOfExecutions;

	public SimpleStatElement(String elementName, double[] timeMeasurements) {
		super(elementName);
		this.timeMeasurements = timeMeasurements;
		this.numberOfExecutions = timeMeasurements.length;
		this.average = -1;
	}

	public SimpleStatElement(String elementName, double average, int numberOfExecutions) {
		super(elementName);
		this.average = average;
		this.numberOfExecutions = numberOfExecutions;
	}

	public double[] getTimeMeasurements() {
		return timeMeasurements;
	}

	public int getNumberOfExecutions() {
		return numberOfExecutions;
	}

	public double getAverage() {
		if (average < 0)
			average = StatisticsUtil.mean(timeMeasurements);

		return average;
	}

	@Override
	public String toString() {
		return getElementName() + ";" + getAverage() + ";" + getNumberOfExecutions();
	}

}
