package br.ufrn.ppgsc.scenario.analyzer.miner.jira;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IQueryIssue;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

public class JiraMiner implements IQueryIssue {
	
	private static final String JIRA_SYSTEM = "WICKET";
	
	public br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue getIssueByNumber(long taskNumber) {
		JiraRestClientFactory restClientFactory = new AsynchronousJiraRestClientFactory();
		
		URI jiraServerUri = null;
		
		try {
			jiraServerUri = new URI("https://issues.apache.org/jira");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		JiraRestClient restClient = restClientFactory.createWithBasicHttpAuthentication(jiraServerUri, "felipeapp", "jisenhara");
		
		Issue issue = restClient.getIssueClient().getIssue(JIRA_SYSTEM + "-" + taskNumber).claim();
		
		br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue issue_model = new br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue();
		
		issue_model.setIssueType(issue.getIssueType().getName());
		issue_model.setNumber(taskNumber);
		issue_model.setIssueId(issue.getId());
		
		try {
			restClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return issue_model;
	}

	public Collection<Long> getIssueNumbersFromMessageLog(String messageLog) {
		Collection<Long> list = new ArrayList<Long>();
		
		// TODO: melhorar isso!
		list.add(Long.parseLong(messageLog.split("-")[1]));
		
		return list;
	}
	
	public static void main(String[] args) {
		
		String log = "WICKET-5801";
		
		JiraMiner miner = new JiraMiner();
		
		Collection<Long> numbers = miner.getIssueNumbersFromMessageLog(log);
		
		for (long num : numbers) {
			br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue issue = miner.getIssueByNumber(num);
			
			System.out.println(issue.getIssueType());
			System.out.println(JIRA_SYSTEM + "-" + issue.getNumber());
			System.out.println(issue.getIssueId());
		}

	}

}
