package br.ufrn.ppgsc.scenario.analyzer.cstatic.model.impl;

import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.PerformanceData;

public class PerformanceDataImpl extends AbstractQADataImpl implements PerformanceData {

	private long limit;

	public long getLimit() {
		return limit;
	}

	public void setLimit(long limit) {
		this.limit = limit;
	}

}
