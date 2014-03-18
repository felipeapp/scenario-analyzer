package br.ufrn.ppgsc.scenario.analyzer.miner.argouml.model;

public enum IssueStatus {
	
	UNCONFIRMED,
	NEW,
	STARTED,
	REOPENED,
	RESOLVED,
	VERIFIED,
	CLOSED;
	
	public String getAsString(IssueStatus issueStatus) {
		return issueStatus.name();
	}
	
}
