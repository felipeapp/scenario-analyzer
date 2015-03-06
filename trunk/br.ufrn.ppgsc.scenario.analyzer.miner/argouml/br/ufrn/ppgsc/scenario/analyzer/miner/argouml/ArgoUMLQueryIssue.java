package br.ufrn.ppgsc.scenario.analyzer.miner.argouml;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IQueryIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.XMLUtil;

public class ArgoUMLQueryIssue implements IQueryIssue {

	private SystemMetadataUtil metadata;

	public ArgoUMLQueryIssue() {
		metadata = SystemMetadataUtil.getInstance();
	}

	@Override
	public Issue getIssueByNumber(long number) {
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("id", "issue_id");
		fields.put("number", "issue_id");
		fields.put("status", "issue_status");
		fields.put("component", "component");
		fields.put("affectedVersion", "version");
		fields.put("creationDate", "creation_ts");
		fields.put("type", "issue_type");
		fields.put("shortDescription", "short_desc");
		
		Issue issue = XMLUtil.getIssueFromRemoteXML(
			metadata.getStringProperty("host") + number,
			"status_message",
			"NotFound",
			new SimpleDateFormat(metadata.getStringProperty("dateFormat")),
			fields
		);
		
		return issue;
	}

	@Override
	public Collection<Long> getIssueNumbersFromMessageLog(String messageLog) {
		Set<Long> result = new HashSet<Long>();

		String words = "(issue|defect|bug|task)";
		String regex1 = words + ".\\d+";
		String regex2 = words + ".\\d+.and.\\d+";
		
		Pattern pattern = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher matcher = pattern.matcher(messageLog);

		while (matcher.find()) {
			String stmt = matcher.group();
			Scanner in = new Scanner(stmt);
			
			// This read the issue word
			in.next();
			
			// this read the number after the issue word
			long issue_number = in.nextLong();
			
			result.add(issue_number);
			in.close();
		}
		
		pattern = Pattern.compile(regex2, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		matcher = pattern.matcher(messageLog);

		while (matcher.find()) {
			String stmt = matcher.group();
			Scanner in = new Scanner(stmt);
			
			// This read the issue word
			in.next();
			
			// this read the first number after the issue word
			long issue_number = in.nextLong();
			result.add(issue_number);
			
			// this read the word and between the numbers
			in.next();
			
			// this read the second number after the word and
			issue_number = in.nextLong();
			result.add(issue_number);
			
			in.close();
		}
		
		return result;
	}

public static void main(String[] args) {
		
		String texto[] = {
			"Issue 570 -",
			"[Issue 6450]",
			"Issue 5601:",
			"Fix for issue 6436:",
			"issue 6169,",
			"issue 6169",
			"issue 5990.",
			"Part of issue 5990 that is",
			"Issue 5959:Further",
			"Issue 6032 and issue 6035",
			"For isSue 6208)",
			"Fix for issue 3990 and issue 3403: Add",
			"Defect 6358 - ",
			"Task 6348",
			"Issue 6048 and 6053",
			"Fixed issue ",
			"This consider issue 6054, issue 4034 and issue 1234",
			"This consider issue 6054, issue 4034 and 1234"
		};
		
		ArgoUMLQueryIssue tracking = new ArgoUMLQueryIssue();
		
		for (String t : texto) {
			System.out.println(t);
			
			for (long id : tracking.getIssueNumbersFromMessageLog(t)) {
				Issue issue = tracking.getIssueByNumber(id);
				System.out.println("\t" + id);
				
				if (issue == null) {
					System.out.println("\tNotFound");
				}
				else {
					System.out.println("\t" + issue.getAffectedVersion());
					System.out.println("\t" + issue.getComponent());
					System.out.println("\t" + issue.getId());
					System.out.println("\t" + issue.getNumber());
					System.out.println("\t" + issue.getShortDescription());
					System.out.println("\t" + issue.getStatus());
					System.out.println("\t" + issue.getType());
					System.out.println("\t" + issue.getCreationDate());
				}
				
				System.out.println("----------------------");
			}
		}

	}

}
