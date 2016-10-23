package br.ufrn.ppgsc.scenario.analyzer.miner.model;

public class DoubleStatElement {
	private String elementName;
	private double TTestPvalue;
	private double UTestPvalue;
	private double AVGv1;
	private double AVGv2;
	private int N1;
	private int N2;

	public DoubleStatElement(String elementName, double TTestPvalue, double UTestPvalue, double AVGv1, double AVGv2, int N1, int N2) {
		this.elementName = elementName;
		this.UTestPvalue = UTestPvalue;
		this.TTestPvalue = TTestPvalue;
		this.AVGv1 = AVGv1;
		this.AVGv2 = AVGv2;
		this.N1 = N1;
		this.N2 = N2;
	}

	public String getElementName() {
		return elementName;
	}

	public double getUTestPvalue() {
		return UTestPvalue;
	}

	public double getTTestPvalue() {
		return TTestPvalue;
	}

	public double getAVGv1() {
		return AVGv1;
	}

	public double getAVGv2() {
		return AVGv2;
	}

	public int getN1() {
		return N1;
	}

	public int getN2() {
		return N2;
	}

}