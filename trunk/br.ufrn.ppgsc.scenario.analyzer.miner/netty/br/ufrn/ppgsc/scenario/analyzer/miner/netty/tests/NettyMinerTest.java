package br.ufrn.ppgsc.scenario.analyzer.miner.netty.tests;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;
import br.ufrn.ppgsc.scenario.analyzer.miner.netty.NettyMiner;

public class NettyMinerTest {

//	@Test
	public void performRequestToGetIssueInfoWithValidId() {
		NettyMiner miner = new NettyMiner();
		Issue issue = miner.getIssueByNumber(2433);
		System.out.println(issue.getIssueId());
		Assert.assertNotNull(issue);
	}
	
	@Test
	public void parseIssueNumberFromMessageLogWithOneNumber() {
		NettyMiner miner = new NettyMiner();
		Collection<Long> issuesId = miner.getIssueNumbersFromMessageLog("[#2339] Reduce memory usage in ProtobufVarint32LengthFieldPrepender");
		Assert.assertEquals(1, issuesId.size());
	}

}
