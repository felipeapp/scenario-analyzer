package br.ufrn.ppgsc.scenario.analyzer.cdynamic.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("performance")
public class RuntimePerformance extends RuntimeGenericAnnotation {

	@Column(name = "limit_time")
	private long limitTime;

	public RuntimePerformance() {

	}

	public RuntimePerformance(String name, long limitTime) {
		super(name);
		this.limitTime = limitTime;
	}

	public long getLimitTime() {
		return limitTime;
	}

	public void setLimitTime(long limitTime) {
		this.limitTime = limitTime;
	}

}
