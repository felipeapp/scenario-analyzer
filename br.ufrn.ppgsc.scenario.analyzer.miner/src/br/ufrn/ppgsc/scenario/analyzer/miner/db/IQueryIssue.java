package br.ufrn.ppgsc.scenario.analyzer.miner.db;

import br.ufrn.ppgsc.scenario.analyzer.miner.sigaa.SINFOIProjectIssue;

public interface IQueryIssue {

	public SINFOIProjectIssue getIssueByNumber(long taskNumber);

	public long getIssueNumberFromMessageLog(String messageLog);

}
