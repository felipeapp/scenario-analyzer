package br.ufrn.ppgsc.scenario.analyzer.miner.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IQueryIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public class GitUpdatedLinesHandler {

	private final Logger logger = Logger.getLogger(GitUpdatedLinesHandler.class);
	
	private static final Map<String, List<Issue>> cache_commit_issues =
			new HashMap<String, List<Issue>>();
	
	private List<UpdatedLine> changedLines;
	private StringBuilder sourceCode;
	private IQueryIssue issueQuery;
	
	private String startRev;
	private String endRev;
	private String filedir;
	private String filename;
	
	public GitUpdatedLinesHandler(String startRev, String endRev, String filedir, String filename) {
		changedLines = new ArrayList<UpdatedLine>();
		sourceCode = new StringBuilder();
		issueQuery = SystemMetadataUtil.getInstance().newObjectFromProperties(IQueryIssue.class);
		
		this.startRev = startRev;
		this.endRev = endRev;
		this.filedir = filedir;
		this.filename = filename;
	}
	
	public List<UpdatedLine> getChangedLines() {
		return Collections.unmodifiableList(changedLines);
	}
	
	public String getSourceCode() {
		return sourceCode.toString();
	}
	
	public void calculateChangedLines() {
		String so_prefix = "cmd /c ";
		String command = so_prefix + "git blame -l " + startRev + ".." + endRev + " " + filename;
		
		try {
			Process p = Runtime.getRuntime().exec(command, null, new File(filedir));
			BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			String line = null;
			while ((line = bf.readLine()) != null) {
				UpdatedLine up = handleLine(line);
				
				if (up != null)
					changedLines.add(up);
			}
			
			bf.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private String getCommitLogMessage(String commit) throws IOException {
		String so_prefix = "cmd /c ";
		String command = so_prefix + "git log --format=%B -n 1 " + commit;
		
		Process p = Runtime.getRuntime().exec(command, null, new File(filedir));
		BufferedReader bf = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		StringBuilder result = new StringBuilder();
		
		String line = null;
		while ((line = bf.readLine()) != null) {
			result.append(line);
			result.append(System.lineSeparator());
		}
		
		return result.toString();
	}

	private UpdatedLine handleLine(String gitblameline) throws IOException {
		Scanner in = new Scanner(gitblameline);
		
		String commit = in.next();
		
		List<String> tokens = new ArrayList<String>();
		while (true) {
			String t = in.next();
			
			if (t.endsWith(")")) {
				t = t.substring(0, t.length() - 1);
				tokens.add(t);
				break;
			}
			
			if (t.startsWith("("))
				t = t.substring(1);

			tokens.add(t);
		}
		
		String source_line = in.nextLine().substring(1);
		
		in.close();
		
		int line_number = Integer.parseInt(tokens.get(tokens.size() - 1));
		
		Date commit_date = null;
		try {
			commit_date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(
					tokens.get(tokens.size() - 4) + " " + tokens.get(tokens.size() - 3));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String author_name = "";
		for (int i = 0; i < tokens.size() - 4; i++)
			author_name += (i == 0 ? "" : " ") + tokens.get(i);
		
		sourceCode.append(source_line + System.lineSeparator());
		
		if (!commit.startsWith("^")) {
			List<Issue> issues = cache_commit_issues.get(commit);
			
			if (issues == null) {
				String path = filedir + filename;
				
				logger.info("\tGetting issues to " + commit + " in " + path);
				
				Issue issue = null;
				issues = new ArrayList<Issue>();
				String logMessage = getCommitLogMessage(commit);
				Collection<Long> issue_numbers = issueQuery.getIssueNumbersFromMessageLog(logMessage);
				
				if (issue_numbers.isEmpty()) {
					logger.warn("\t[Empty] No issues for log message: " + logMessage);
					
					issue = new Issue();
					issue.setNumber(0);
					
					issues.add(issue);
				}
				else {
					for (Long issue_number : issue_numbers) {
						if (issue_number < 0) {
							logger.warn("\t[Invalid: " + issue_number + "] No issues for log message: " + logMessage);
							issue = new Issue();
							issue.setNumber(0);
						}
						else {
							logger.info("\t[Found: " + issue_number + "] In log message: " + logMessage);
							issue = issueQuery.getIssueByNumber(issue_number);
						}
						
						issues.add(issue);
					}
				}
				
				cache_commit_issues.put(commit, issues);
			}
			
			return new UpdatedLine(commit_date, commit, issues, author_name, source_line, line_number);
		}
		
		return null;
	}

	public static void main(String[] args) throws IOException {
//		GitUpdatedLinesHandler gitHandler = new GitUpdatedLinesHandler(
//				"c5d8af446a39db10a1744d47e5a466fa1c87a374",
//				"b562148e2d8d0f0487495fb5dd2d5de62306c5e0",
//				"C:/Users/Felipe/git/netty/transport/src/main/java/io/netty/channel/",
//				"DefaultChannelHandlerContext.java");
		
		GitUpdatedLinesHandler gitHandler = new GitUpdatedLinesHandler(
				"7f0dc74db4ed30e2831c439d10fe0244813bce3e",
				"021348160abf428bee0be2eca770cd08142ad168",
				"C:/Users/Felipe/git/wicket/wicket-core/src/main/java/org/apache/wicket/",
				"MarkupContainer.java");
		
		gitHandler.calculateChangedLines();
		
		Collection<UpdatedLine> list = gitHandler.getChangedLines();
		
		for (UpdatedLine up_line : list) {
			System.out.println(up_line.getAuthor());
			System.out.println(up_line.getLine());
			System.out.println(up_line.getLineNumber());
			System.out.println(up_line.getRevision());
			System.out.println(up_line.getDate());
			
			for (Issue i : up_line.getIssues()) {
				System.out.println("\t" + i.getIssueId());
				System.out.println("\t" + i.getNumber());
				System.out.println("\t" + i.getIssueType());
			}
			
			System.out.println("------------------------------");
		}
		
		System.out.println("####################");
		System.out.println(gitHandler.getSourceCode());
		System.out.println("####################");
	}

}
