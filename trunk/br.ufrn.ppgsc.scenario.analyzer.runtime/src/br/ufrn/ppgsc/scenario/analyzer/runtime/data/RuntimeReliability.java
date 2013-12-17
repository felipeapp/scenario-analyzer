package br.ufrn.ppgsc.scenario.analyzer.runtime.data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("reliability")
public class RuntimeReliability extends RuntimeGenericAnnotation {

	@Column(name = "failure_rate")
	private double failureRate;

	public RuntimeReliability() {

	}

	public RuntimeReliability(String name, double failureRate) {
		super(name);
		this.failureRate = failureRate;
	}

	public double getFailureRate() {
		return failureRate;
	}

	public RuntimeReliability(double failureRate) {
		this.failureRate = failureRate;
	}

	public void setFailureRate(double failureRate) {
		this.failureRate = failureRate;
	}

}
