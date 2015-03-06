package br.ufrn.ppgsc.scenario.analyzer.miner.netty.tests;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import br.ufrn.ppgsc.scenario.analyzer.miner.git.GithubQueryIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;

public class NettyMinerTest {

	@Test
	public void performRequestToGetIssueInfoWithValidId() {
		GithubQueryIssue miner = new GithubQueryIssue();
		Issue issue = miner.getIssueByNumber(2330);
		Assert.assertNotNull(issue);
	}
	
	@Test
	public void performRequestToGetIssueInfoWithValidId2() {
		GithubQueryIssue miner = new GithubQueryIssue();
		Issue issue = miner.getIssueByNumber(2300);
		Assert.assertNotNull(issue);
	}
	
	@Test
	public void parseIssueNumberFromMessageLogWithOneNumber() {
		GithubQueryIssue miner = new GithubQueryIssue();
		Collection<Long> issuesId = miner.getIssueNumbersFromMessageLog("[#2339] Reduce memory usage in ProtobufVarint32LengthFieldPrepender");
		Assert.assertEquals(1, issuesId.size());
	}

}
