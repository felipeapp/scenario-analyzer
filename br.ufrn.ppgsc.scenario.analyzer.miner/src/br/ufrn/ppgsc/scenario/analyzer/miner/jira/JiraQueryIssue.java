package br.ufrn.ppgsc.scenario.analyzer.miner.jira;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IQueryIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

public class JiraQueryIssue implements IQueryIssue {
	
	private String jira_system;
	private String jira_url;
	
	public JiraQueryIssue() {
		jira_system = SystemMetadataUtil.getInstance().getStringProperty("jira_system");
		jira_url = SystemMetadataUtil.getInstance().getStringProperty("jira_url");
		
		if (jira_system == null || jira_url == null)
			throw new NullPointerException("Jira property is null.");
	}

	public String getJiraSystem() {
		return jira_system;
	}

	public String getJiraURL() {
		return jira_url;
	}

	public br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue getIssueByNumber(long taskNumber) {
		JiraRestClientFactory restClientFactory = new AsynchronousJiraRestClientFactory();
		
		URI jiraServerUri = null;
		
		try {
			jiraServerUri = new URI(jira_url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		JiraRestClient restClient = restClientFactory.createWithBasicHttpAuthentication(jiraServerUri, "felipeapp", "jisenhara");
		
		Issue issue = null;
		br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue issue_model = new br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue();
		
		try {
			issue = restClient.getIssueClient().getIssue(jira_system + "-" + taskNumber).claim();
			
			issue_model.setIssueType(issue.getIssueType().getName());
			issue_model.setNumber(taskNumber);
			issue_model.setIssueId(issue.getId());
		} catch (RestClientException e) {
			System.err.println(e.getMessage());
		}

		try {
			restClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return issue_model;
	}

	public Collection<Long> getIssueNumbersFromMessageLog(String messageLog) {
		Set<Long> result = new HashSet<Long>();

		String word = jira_system + "-";
		String regex = word +  "\\s*\\d+";
		
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher matcher = pattern.matcher(messageLog);

		while (matcher.find()) {
			String stmt = matcher.group();
			BufferedReader br = new BufferedReader(new StringReader(stmt));
			
			// This read the issue word
			String issueStr = null;
			try {
				issueStr = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// This remove the issue work and convert to a number
			long issue_number = Long.parseLong(issueStr.toUpperCase().replace(word, "").trim());
			result.add(issue_number);
			
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static void main(String[] args) {
		
		String[] logs = {
			"WICKet-6",
			"asdas as d WICKET-5801 asd asd as a",
			"Update the year in NOTICE to 2012",
			"WICKET-4439 Move classes around so that there are no two packages wit…\n…h the same name in different modules\r\nMove o.a.w.serialize.ISerializer from -util to -core.",
			"bla bla bla WICKET-3697 ",
			"bla bla bla WICKET-5811",
			"WICKET-5713",
			"  WICKET-5775   ",
			"WICKET- 5713 ",
			"WICKET- 58 09", // In this case the key will be WICKET-58
			"WICKET-105809"
		};
		
		JiraQueryIssue miner = new JiraQueryIssue();
		
		for (String log : logs) {
			System.out.println("Mining log: " + log);
			Collection<Long> numbers = miner.getIssueNumbersFromMessageLog(log);
			
			for (long num : numbers) {
				System.out.println("\tGetting issue " + miner.getJiraSystem() + "-" + num);
				
				br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue issue = miner.getIssueByNumber(num);
				
				System.out.println("\t\t" + issue.getIssueType());
				System.out.println("\t\t" + miner.getJiraSystem() + "-" + issue.getNumber());
				System.out.println("\t\t" + issue.getIssueId());
			}
		}

	}

}
