package br.ufrn.ppgsc.scenario.analyzer.miner.issues;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jdom2.JDOMException;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IQueryIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.HTTPSUtil;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public class GithubQueryIssue implements IQueryIssue {
	
	private SystemMetadataUtil metadata;
	
	public GithubQueryIssue() {
		metadata = SystemMetadataUtil.getInstance();
	}

	private JSONObject getJSONByIssueId(long number) throws IOException, JDOMException, JSONException {
		String url = metadata.getStringProperty("host") + number +
				"?client_id=" + metadata.getStringProperty("client_id") +
				"&client_secret=" + metadata.getStringProperty("client_secret");

		String json_text;
		InputStreamReader isr = HTTPSUtil.getInputStreamReader(url);
		
		if (isr == null) {
			json_text = "{\"message\": \"Not Found\",\"documentation_url\": \"https://developer.github.com/v3\"}";
		}
		else {
			BufferedReader br = new BufferedReader(isr);
			json_text = br.readLine();
		}

		return new JSONObject(json_text);
	}

	private Issue getIssueInfoFromJSON(JSONObject json) throws JSONException, ParseException {
		Issue issue = null;
		boolean issue_exists = true;
		
		try {
			String message = json.getString("message");
			
			if (message != null && message.equals("Not Found"))
				issue_exists = false;
		} catch (JSONException e) {
			// Just continue because message was not found, but the issue exists
		}
		
		if (issue_exists) {
			issue = new Issue();
			SimpleDateFormat sdf = new SimpleDateFormat(metadata.getStringProperty("dateFormat"));
	
			issue.setAffectedVersion("milestone");
			issue.setComponent("component");
			
			String date = json.getString("created_at").replace('T', ' ').replace('Z', ' ');
			issue.setCreationDate(sdf.parse(date));
			
			issue.setId(json.getInt("id"));
			issue.setNumber(json.getInt("number"));
			issue.setStatus(json.getString("state"));
			
			if (json.getJSONArray("labels") != null) {
				String issueType = "";
				
				for (int i = 0; i < json.getJSONArray("labels").length(); i++) {
					String type = json.getJSONArray("labels").getJSONObject(i).getString("name");
	
					if (type.equalsIgnoreCase("defect"))
						issue.setBugFixing(true);
					
					if (issueType.isEmpty())
						issueType = type;
					else
						issueType += ("," + type);
				}
				
				issue.setType(issueType.isEmpty() ? "none yet" : issueType);
			}
			
			issue.setShortDescription(json.getString("title"));
		}

		return issue;
	}

	@Override
	public Issue getIssueByNumber(long number) {
		try {
			return getIssueInfoFromJSON(getJSONByIssueId(number));
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Collection<Long> getIssueNumbersFromMessageLog(String messageLog) {
		Set<Long> result = new HashSet<Long>();

		char word = '#';
		String regex1 = word +  "\\d+";
		
		Pattern pattern = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher matcher = pattern.matcher(messageLog);

		while (matcher.find()) {
			String stmt = matcher.group();
			Scanner in = new Scanner(stmt);
			
			// This read the issue word
			String issueStr = in.next();
			
			// this read the first number after the issue word
			long issue_number = Long.parseLong(issueStr.replace(word, ' ').trim());
			result.add(issue_number);
			
			in.close();
		}
		
		return result;
	}

}
