package br.ufrn.ppgsc.scenario.analyzer.miner.model;

import br.ufrn.ppgsc.scenario.analyzer.miner.util.StatisticsUtil;

public class SimpleStatElement {

	// Element name (scenario name or method name)
	private String elementName;

	// Average execution time
	private double average;

	// Every time of the element
	private double[] timeMeasurements;

	public SimpleStatElement(String elementName, double[] timeMeasurements) {
		this.elementName = elementName;
		this.timeMeasurements = timeMeasurements;
		this.average = -1;
	}

	public String getElementName() {
		return elementName;
	}

	public double[] getTimeMeasurements() {
		return timeMeasurements;
	}

	public int getNumberOfExecutions() {
		return timeMeasurements.length;
	}

	public double getAverage() {
		if (average < 0)
			average = StatisticsUtil.mean(timeMeasurements);

		return average;
	}

}
