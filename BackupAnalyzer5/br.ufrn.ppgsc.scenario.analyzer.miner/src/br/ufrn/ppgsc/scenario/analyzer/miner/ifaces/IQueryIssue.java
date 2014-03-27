package br.ufrn.ppgsc.scenario.analyzer.miner.ifaces;

public interface IQueryIssue {

	public IContentIssue getIssueByNumber(long taskNumber);

	public long getIssueNumberFromMessageLog(String messageLog);

}
