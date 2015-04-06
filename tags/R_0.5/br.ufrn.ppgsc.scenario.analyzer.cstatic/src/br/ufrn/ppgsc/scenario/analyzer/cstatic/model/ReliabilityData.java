package br.ufrn.ppgsc.scenario.analyzer.cstatic.model;

public interface ReliabilityData extends AbstractQAData {

	public double getFailureRate();

	public void setFailureRate(double failure_rate);
	
}
