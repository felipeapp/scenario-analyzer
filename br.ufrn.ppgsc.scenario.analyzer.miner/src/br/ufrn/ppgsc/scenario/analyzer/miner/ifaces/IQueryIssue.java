package br.ufrn.ppgsc.scenario.analyzer.miner.ifaces;

import java.util.Collection;

import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;

public interface IQueryIssue {

	public Issue getIssueByNumber(long taskNumber);

	public Collection<Long> getIssueNumbersFromMessageLog(String messageLog);

}
