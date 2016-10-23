package br.ufrn.ppgsc.scenario.analyzer.miner.model;

public class SimpleStatElement {

	// Element name (scenario name or method name)
	private String elementName;

	// Average execution time
	private double average;

	// Number of executions
	private int numberOfExecutions;

	public SimpleStatElement(String elementName, double average, int numberOfExecutions) {
		super();
		this.elementName = elementName;
		this.average = average;
		this.numberOfExecutions = numberOfExecutions;
	}

	public String getElementName() {
		return elementName;
	}

	public double getAverage() {
		return average;
	}

	public int getNumberOfExecutions() {
		return numberOfExecutions;
	}

}
