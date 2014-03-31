package br.ufrn.ppgsc.scenario.analyzer.miner.ifaces;

import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;

public interface IQueryIssue {

	public Issue getIssueByNumber(long taskNumber);

	public long getIssueNumberFromMessageLog(String messageLog);

}
