package br.ufrn.ppgsc.scenario.analyzer.miner.bugzilla;

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

public class BugzillaQueryIssue implements IQueryIssue {

	private SystemMetadataUtil metadata;

	public BugzillaQueryIssue() {
		metadata = SystemMetadataUtil.getInstance();
	}

	@Override
	public Issue getIssueByNumber(long number) {
		Map<String, String> fields = new HashMap<String, String>();
		fields.put("id", "bug_id");
		fields.put("number", "bug_id");
		fields.put("status", "bug_status");
		fields.put("component", "component");
		fields.put("affectedVersion", "version");
		fields.put("creationDate", "creation_ts");
		fields.put("type", "bug_severity");
		fields.put("shortDescription", "short_desc");
		
		Issue issue = XMLUtil.getIssueFromRemoteXML(
			metadata.getStringProperty("host") + number,
			"error",
			"NotFound",
			new SimpleDateFormat(metadata.getStringProperty("dateFormat")),
			fields
		);
		
		return issue;
	}

	// TODO: é possível mais de um número de issue por commit?
	@Override
	public Collection<Long> getIssueNumbersFromMessageLog(String messageLog) {
		Set<Long> result = new HashSet<Long>();

		Pattern pattern = Pattern.compile("^\\d+", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		Matcher matcher = pattern.matcher(messageLog);

		while (matcher.find()) {
			String stmt = matcher.group();
			Scanner in = new Scanner(stmt);
			
			long issue_number = in.nextLong();
			
			result.add(issue_number);
			in.close();
		}
		
		return result;
	}

	public static void main(String[] args) {
		
		String texto[] = {
			"459681 Remove Glassfish JSP in favor of Apache JSP",
			"461350 Update HttpParser IllegalCharacter handling to RFC7230",
			"SpinLock cleanup of HttpExchange",
			"45968100", // não existe
			"461052 - Local streams created after INITIAL_WINDOW_SIZE setting have… wrong send window.",
			"461452",
			"461452Double release of buffer by HttpReceiverOverHTTP"
		};
		
		BugzillaQueryIssue tracking = new BugzillaQueryIssue();
		
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
