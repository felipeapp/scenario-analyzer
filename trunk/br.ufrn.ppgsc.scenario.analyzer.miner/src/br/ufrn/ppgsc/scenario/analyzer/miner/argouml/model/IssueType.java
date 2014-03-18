package br.ufrn.ppgsc.scenario.analyzer.miner.argouml.model;

public enum IssueType {

	DEFECT,
	ENHANCEMENT,
	FEATURE,
	TASK,
	PATCH;
	
	public String getAsString(IssueType issueType) {
		return issueType.name();
	}
	
}
