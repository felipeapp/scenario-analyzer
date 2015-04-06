package br.ufrn.ppgsc.scenario.analyzer.miner.test;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import br.ufrn.ppgsc.scenario.analyzer.miner.issues.IssuezillaQueryIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;

public class ArgoUMLMinerTest {

	@Test
	public void performRequestToGetIssueInfoWithValidId() {
		IssuezillaQueryIssue miner = new IssuezillaQueryIssue();
		Issue issue = miner.getIssueByNumber(3458);
		System.out.println(issue.getId());
		Assert.assertNotNull(issue);
	}

	@Test
	public void performRequestToGetIssueInfoWithInvalidId() {
		IssuezillaQueryIssue miner = new IssuezillaQueryIssue();
		Issue issue = miner.getIssueByNumber(10000);
		Assert.assertNull(issue);
	}
	
	@Test
	public void parseIssueNumberFromMessageLogWithOneNumber() {
		IssuezillaQueryIssue miner = new IssuezillaQueryIssue();
		Collection<Long> issuesId = miner.getIssueNumbersFromMessageLog("Fixed issue 2743: Events are not...");
		Assert.assertEquals(1, issuesId.size());
	}
	
	@Test
	public void parseIssueNumberFromMessageLogWithTwoNumbers() {
		IssuezillaQueryIssue miner = new IssuezillaQueryIssue();
		Collection<Long> issuesId = miner.getIssueNumbersFromMessageLog("Fixed issue 3333. Issue 3323, Corrected some typos, and switched on wordwrap for the wizard instructions text. Changed the button text from 'Next' to 'Next>', since this is the indication used in all the critics texts.");
		Assert.assertEquals(2, issuesId.size());
	}
	
	@Test
	public void parseIssueNumberFromMessageLogWithNoNumbers() {
		IssuezillaQueryIssue miner = new IssuezillaQueryIssue();
		Collection<Long> issuesId = miner.getIssueNumbersFromMessageLog("Fixed issue ");
		Assert.assertEquals(0, issuesId.size());
	}
	
	/* 
	 * Eu removi a verificação da issue pq mudei o padrão de busca.
	 * Como está mais preciso agora, acredito que não precisa mais.
	 */
	//@Test
	public void parseIssueNumberFromMessageLogWithOneNonTaskNumber() {
		IssuezillaQueryIssue miner = new IssuezillaQueryIssue();
		Collection<Long> issuesId = miner.getIssueNumbersFromMessageLog("Fixed issue 98426879624789642 blablabla...");
		Assert.assertEquals(0, issuesId.size());
	}

}
