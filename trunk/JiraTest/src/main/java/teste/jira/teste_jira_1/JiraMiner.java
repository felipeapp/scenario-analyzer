package teste.jira.teste_jira_1;

import java.net.URI;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

public class JiraMiner {

	public static void main(String[] args) throws Exception {

		JiraRestClientFactory restClientFactory = new AsynchronousJiraRestClientFactory();
		URI jiraServerUri = new URI("https://issues.apache.org/jira");
		JiraRestClient restClient = restClientFactory.createWithBasicHttpAuthentication(jiraServerUri, "felipeapp", "jisenhara");

		Issue issue = restClient.getIssueClient().getIssue("WICKET-5801").get();

		System.out.println(issue.getIssueType().getName());
		System.out.println(issue.getKey());
		System.out.println(issue.getId());

		restClient.close();

	}

}
