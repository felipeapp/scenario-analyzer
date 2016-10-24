package br.ufrn.ppgsc.scenario.analyzer.miner.model;

import br.ufrn.ppgsc.scenario.analyzer.miner.util.StatisticsUtil;

public class DoubleStatElement {
	private String elementName;

	private double tTestPvalue;
	private double uTestPvalue;

	private double[] timeMeasurementsV1;
	private double[] timeMeasurementsV2;

	private double averageV1;
	private double averageV2;

	public DoubleStatElement(String elementName, double tTestPvalue, double uTestPvalue, double[] timeMeasurementsV1,
			double[] timeMeasurementsV2) {
		this.elementName = elementName;

		this.tTestPvalue = tTestPvalue;
		this.uTestPvalue = uTestPvalue;

		this.timeMeasurementsV1 = timeMeasurementsV1;
		this.timeMeasurementsV2 = timeMeasurementsV2;

		averageV1 = averageV2 = -1;
	}

	public String getElementName() {
		return elementName;
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
		return timeMeasurementsV1.length;
	}

	public int getNumberOfExecutionsV2() {
		return timeMeasurementsV2.length;
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

}