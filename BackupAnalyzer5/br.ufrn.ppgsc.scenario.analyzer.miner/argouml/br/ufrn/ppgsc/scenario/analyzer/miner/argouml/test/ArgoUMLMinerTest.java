package br.ufrn.ppgsc.scenario.analyzer.miner.argouml.test;

import org.eclipse.core.internal.dtree.ObjectNotFoundException;
import org.junit.Assert;
import org.junit.Test;

import br.ufrn.ppgsc.scenario.analyzer.miner.argouml.ArgoUMLMiner;
import br.ufrn.ppgsc.scenario.analyzer.miner.argouml.model.Issue;

public class ArgoUMLMinerTest {

	@Test
	public void performRequestToGetIssueInfoWithValidId() {
		ArgoUMLMiner miner = new ArgoUMLMiner();
		Issue issue = miner.getIssueInfoByIssueId(3458);
		Assert.assertNotNull(issue);
	}

	@Test(expected = ObjectNotFoundException.class)
	public void performRequestToGetIssueInfoWithInvalidId() {
		ArgoUMLMiner miner = new ArgoUMLMiner();
		Issue issue = miner.getIssueInfoByIssueId(10000);
		Assert.assertNotNull(issue);
	}

	@Test(expected = IllegalArgumentException.class)
	public void performRequestToGetIssueInfoWithNullId() {
		ArgoUMLMiner miner = new ArgoUMLMiner();
		Issue issue = miner.getIssueInfoByIssueId(null);
		Assert.assertNotNull(issue);
	}

}
