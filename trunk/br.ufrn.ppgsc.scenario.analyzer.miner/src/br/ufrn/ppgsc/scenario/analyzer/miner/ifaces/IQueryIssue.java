package br.ufrn.ppgsc.scenario.analyzer.miner.ifaces;

import java.util.List;

import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;

public interface IQueryIssue {

	public Issue getIssueByNumber(long taskNumber);

	public List<Long> getIssueNumbersFromMessageLog(String messageLog);

}
