package br.ufrn.ppgsc.scenario.analyzer.miner.model;

import br.ufrn.ppgsc.scenario.analyzer.miner.util.StatisticsUtil;

public class DoubleStatElement extends AbstractStatElement {

	public static final String HEADER = "Name;P-Value (TTest);P-Value (UTest);AVG R1;AVG R2;N1;N2;Delta";

	private double tTestPvalue;
	private double uTestPvalue;

	private double[] timeMeasurementsV1;
	private double[] timeMeasurementsV2;

	private int numberOfExecutionsV1;
	private int numberOfExecutionsV2;

	private double averageV1;
	private double averageV2;

	public DoubleStatElement(String elementName, double tTestPvalue, double uTestPvalue, double[] timeMeasurementsV1,
			double[] timeMeasurementsV2) {
		super(elementName);

		this.tTestPvalue = tTestPvalue;
		this.uTestPvalue = uTestPvalue;

		this.timeMeasurementsV1 = timeMeasurementsV1;
		this.timeMeasurementsV2 = timeMeasurementsV2;

		this.numberOfExecutionsV1 = timeMeasurementsV1.length;
		this.numberOfExecutionsV2 = timeMeasurementsV2.length;

		this.averageV1 = this.averageV2 = -1;
	}

	public DoubleStatElement(String elementName, double tTestPvalue, double uTestPvalue, double averageV1,
			double averageV2, int numberOfExecutionsV1, int numberOfExecutionsV2) {
		super(elementName);
		this.tTestPvalue = tTestPvalue;
		this.uTestPvalue = uTestPvalue;
		this.numberOfExecutionsV1 = numberOfExecutionsV1;
		this.numberOfExecutionsV2 = numberOfExecutionsV2;
		this.averageV1 = averageV1;
		this.averageV2 = averageV2;
	}

	public double getTTestPvalue() {
		return tTestPvalue;
	}

	public double getUTestPvalue() {
		return uTestPvalue;
	}

	public double[] getTimeMeasurementsV1() {
		return timeMeasurementsV1;
	}

	public double[] getTimeMeasurementsV2() {
		return timeMeasurementsV2;
	}

	public int getNumberOfExecutionsV1() {
		return numberOfExecutionsV1;
	}

	public int getNumberOfExecutionsV2() {
		return numberOfExecutionsV2;
	}

	public double getAverageV1() {
		if (averageV1 < 0)
			averageV1 = StatisticsUtil.mean(timeMeasurementsV1);

		return averageV1;
	}

	public double getAverageV2() {
		if (averageV2 < 0)
			averageV2 = StatisticsUtil.mean(timeMeasurementsV2);

		return averageV2;
	}

	@Override
	public String toString() {
		return getElementName() + ";" + getTTestPvalue() + ";" + getUTestPvalue() + ";" + getAverageV1() + ";"
				+ getAverageV2() + ";" + getNumberOfExecutionsV1() + ";" + getNumberOfExecutionsV2() + ";"
				+ (getAverageV2() - getAverageV1());
	}

}