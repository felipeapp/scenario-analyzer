package br.ufrn.ppgsc.scenario.analyzer.miner.netty;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
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
import br.ufrn.ppgsc.scenario.analyzer.miner.util.HttpsUtil;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public class NettyMiner implements IQueryIssue {
	
	enum IssueType {
		DEFECT, FEATURE, IMPROVEMENT;
	}

	private SystemMetadataUtil metadata;
	
	public NettyMiner() {
		metadata = SystemMetadataUtil.getInstance();
	}

	private JSONObject getJSONByIssueId(Long issueId) throws IOException, JDOMException, JSONException {
		String url = metadata.getStringProperty("host") + issueId + "?client_id=" + metadata.getStringProperty("client_id") +
				"&client_secret=" + metadata.getStringProperty("client_secret");
		InputStream inputStream = HttpsUtil.getInputStreamFixHttps(url);
		InputStreamReader isr = new InputStreamReader(inputStream);

		LineNumberReader lineNumberReader = new LineNumberReader(isr);
		
		JSONObject json = new JSONObject(lineNumberReader.readLine());

		return json;
	}

	/**
	 * Método que verifica se o argumento passado é diferente de nulo. Se não
	 * for, uma exceção é lançada.
	 * 
	 * @param issueId
	 * @throws IllegalArgumentException
	 */
	private void verifyValidArguments(Long issueId) throws IllegalArgumentException {
		if (issueId == null)
			throw new IllegalArgumentException("O id da tarefa está nulo");
	}

	private Issue getIssueInfoFromJSON(JSONObject json) throws JSONException, ParseException {
		Issue issue = null;
		SimpleDateFormat sdf = new SimpleDateFormat(metadata.getStringProperty("dateFormat"));

		issue = new Issue();

		issue.setAffectedVersion(null);
		issue.setComponent("component");
		String date = json.getString("created_at");
		date = date.replace('T', ' ');
		date = date.replace('Z', ' ');
		issue.setDateCreation(sdf.parse(date));
		issue.setIssueId(json.getInt("number"));
		issue.setNumber(json.getInt("number"));
		issue.setIssueStatus(json.getString("state"));
		
		if (json.getJSONArray("labels") != null) {
			String issueType = "";
			
			for (int i = 0; i < json.getJSONArray("labels").length(); i++) {
				String type = json.getJSONArray("labels").getJSONObject(i).getString("name");
				try {
					if (IssueType.valueOf(type.toUpperCase()) instanceof IssueType) {
						if (issueType != null && issueType.isEmpty()) {
							issueType += type;
						} else {
							issueType += ("," + type);
						}
					}
				} catch (Exception e) {
					// não faz nada, apenas passa pro próximo indice do for
				}
			}
			
			issue.setIssueType(issueType.isEmpty() ? "none yet" : issueType);
		}
		
		issue.setShortDescription(json.getString("title"));

		return issue;
	}

	@Override
	public Issue getIssueByNumber(long taskNumber) {
		Issue issue = null;
		
		try {
			verifyValidArguments(taskNumber);
			JSONObject json = getJSONByIssueId(taskNumber);
			issue = getIssueInfoFromJSON(json);
		} catch (IOException | JDOMException | JSONException | ParseException e) {
			e.printStackTrace();
		}
		
		return issue;
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
