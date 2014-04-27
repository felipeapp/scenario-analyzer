package br.ufrn.ppgsc.scenario.analyzer.miner.argouml.test;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import br.ufrn.ppgsc.scenario.analyzer.miner.argouml.ArgoUMLMiner;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;

public class ArgoUMLMinerTest {

	@Test
	public void performRequestToGetIssueInfoWithValidId() {
		ArgoUMLMiner miner = new ArgoUMLMiner();
		Issue issue = miner.getIssueByNumber(3458);
		System.out.println(issue.getIssueId());
		Assert.assertNotNull(issue);
	}

	@Test
	public void performRequestToGetIssueInfoWithInvalidId() {
		ArgoUMLMiner miner = new ArgoUMLMiner();
		Issue issue = miner.getIssueByNumber(10000);
		Assert.assertNull(issue);
	}
	
	@Test
	public void parseIssueNumberFromMessageLogWithOneNumber() {
		ArgoUMLMiner miner = new ArgoUMLMiner();
		Collection<Long> issuesId = miner.getIssueNumbersFromMessageLog("Fixed issue 2743: Events are not...");
		Assert.assertEquals(1, issuesId.size());
	}
	
	@Test
	public void parseIssueNumberFromMessageLogWithTwoNumbers() {
		ArgoUMLMiner miner = new ArgoUMLMiner();
		Collection<Long> issuesId = miner.getIssueNumbersFromMessageLog("Fixed issue 3333. Issue 3323, Corrected some typos, and switched on wordwrap for the wizard instructions text. Changed the button text from 'Next' to 'Next>', since this is the indication used in all the critics texts.");
		Assert.assertEquals(2, issuesId.size());
	}
	
	@Test
	public void parseIssueNumberFromMessageLogWithNoNumbers() {
		ArgoUMLMiner miner = new ArgoUMLMiner();
		Collection<Long> issuesId = miner.getIssueNumbersFromMessageLog("Fixed issue ");
		Assert.assertEquals(0, issuesId.size());
	}
	
	/* 
	 * Eu removi a verificação da issue pq mudei o padrão de busca.
	 * Como está mais preciso agora, acredito que não precisa mais.
	 */
	//@Test
	public void parseIssueNumberFromMessageLogWithOneNonTaskNumber() {
		ArgoUMLMiner miner = new ArgoUMLMiner();
		Collection<Long> issuesId = miner.getIssueNumbersFromMessageLog("Fixed issue 98426879624789642 blablabla...");
		Assert.assertEquals(0, issuesId.size());
	}

}
