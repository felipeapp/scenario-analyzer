package br.ufrn.ppgsc.scenario.analyzer.miner.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
		String firstName = in.next().replace("(", "");
		String lastName = in.next();
		
		@SuppressWarnings("unused") String datetime;
		if (lastName.startsWith("20")) {
			datetime = lastName + " " + in.next();
			lastName = "";
		}
		else {
			datetime = in.next() + " " + in.next();
		}
		
		@SuppressWarnings("unused") String timezone = in.next();
		int lineNumber = Integer.parseInt(in.next().replace(")", ""));
		String sourceLine = in.nextLine().substring(1);
		
		in.close();
		
		sourceCode.append(sourceLine + System.lineSeparator());
		
		if (!commit.startsWith("^")) {
			List<Issue> issues = cache_commit_issues.get(commit);
			
			if (issues == null) {
				logger.info("Inside handler, getting issues to commit " + commit);

				String path = filedir + filename;
				
				Issue issue = null;
				issues = new ArrayList<Issue>();
				String logMessage = getCommitLogMessage(commit);
				Collection<Long> issue_numbers = issueQuery.getIssueNumbersFromMessageLog(logMessage);
				
				if (issue_numbers.isEmpty()) {
					logger.warn("[IssueListEmpty] LogMessage: " + logMessage);
					logger.warn("[IssueListEmpty] Path: " + path + ", commit = " + commit);
					
					issue = new Issue();
					issue.setNumber(-1);
					
					issues.add(issue);
				}
				else {
					for (Long issue_number : issue_numbers) {
						if (issue_number < 0) {
							logger.warn("[NotFoundIssue] LogMessage: " + logMessage);
							logger.warn("[NotFoundIssue] Path: " + path + ", commit = " + commit + ", task number = " + issue_number);
							
							issue = new Issue();
							issue.setNumber(-1);
						}
						else {
							logger.info("[FoundIssue] LogMessage: " + logMessage);
							logger.info("[FoundIssue] Path: " + path + ", commit = " + commit + ", task number = " + issue_number);
							
							issue = issueQuery.getIssueByNumber(issue_number);
						}
						
						issues.add(issue);
					}
				}
				
				cache_commit_issues.put(commit, issues);
			}
			
			return new UpdatedLine(null, commit, issues, firstName + " " + lastName, sourceLine, lineNumber);
		}
		
		return null;
	}

	public static void main(String[] args) throws IOException {
		GitUpdatedLinesHandler gitHandler = new GitUpdatedLinesHandler(
				"8acba6063eabd9fafec57c5abcfa3dd79285bbdf",
				"f696c98ff8fe23dce4f2046cdb7d8483fc24cc8a",
				"C:/Users/Felipe/git/netty/transport/src/main/java/io/netty/channel/",
				"DefaultChannelHandlerContext.java");
		
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
