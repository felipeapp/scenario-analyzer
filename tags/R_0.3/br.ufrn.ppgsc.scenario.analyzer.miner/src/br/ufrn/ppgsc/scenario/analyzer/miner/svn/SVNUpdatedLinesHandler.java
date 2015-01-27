package br.ufrn.ppgsc.scenario.analyzer.miner.svn;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNRevisionProperty;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.ISVNAnnotateHandler;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IQueryIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public class SVNUpdatedLinesHandler implements ISVNAnnotateHandler {

	private final Logger logger = Logger.getLogger(SVNUpdatedLinesHandler.class);
	
	private static final Map<Long, List<Issue>> cache_revision_issues =
			new HashMap<Long, List<Issue>>();
	
	private List<UpdatedLine> changedLines;
	private StringBuilder sourceCode;
	private IQueryIssue issueQuery;
	
	private String path;
	private SVNRepository repository;
	
	public SVNUpdatedLinesHandler(SVNRepository repository, String path) {
		changedLines = new ArrayList<UpdatedLine>();
		sourceCode = new StringBuilder();
		issueQuery = SystemMetadataUtil.getInstance().newObjectFromProperties(IQueryIssue.class);
		
		this.path = path;
		this.repository = repository;
	}
	
	public List<UpdatedLine> getChangedLines() {
		return Collections.unmodifiableList(changedLines);
	}
	
	public String getSourceCode() {
		return sourceCode.toString();
	}
	
	public void handleEOF() throws SVNException {
		System.out.println("EOF has just been found!");
	}

	public void handleLine(Date date, long revision, String author, String line) throws SVNException {
		handleLine(date, revision, author, line, null, -1, null, null, -1);
	}

	public void handleLine(Date date, long revision, String author, String line, Date mergedDate,
			long mergedRevision, String mergedAuthor, String mergedPath, int lineNumber) throws SVNException {
		
		sourceCode.append(line + System.lineSeparator());
		
		if (revision != -1) {
			List<Issue> issues = cache_revision_issues.get(revision);
			
			if (issues == null) {
				logger.info("Inside handler, getting tasks to revision " + revision);

				Issue issue = null;
				issues = new ArrayList<Issue>();
				String logMessage = repository.getRevisionPropertyValue(revision, SVNRevisionProperty.LOG).getString();
				Collection<Long> issue_numbers = issueQuery.getIssueNumbersFromMessageLog(logMessage);
				
				if (issue_numbers.isEmpty()) {
					logger.warn("[IssueListEmpty] LogMessage: " + logMessage);
					logger.warn("[IssueListEmpty] Path: " + path + ", revision = " + revision);
					
					issue = new Issue();
					issue.setNumber(-1);
					
					issues.add(issue);
				}
				else {
					for (Long issue_number : issue_numbers) {
						if (issue_number < 0) {
							logger.warn("[NotFoundIssue] LogMessage: " + logMessage);
							logger.warn("[NotFoundIssue] Path: " + path + ", revision = " + revision + ", task number = " + issue_number);
							
							issue = new Issue();
							issue.setNumber(-1);
						}
						else {
							logger.info("[FoundIssue] LogMessage: " + logMessage);
							logger.info("[FoundIssue] Path: " + path + ", revision = " + revision + ", task number = " + issue_number);
							
							issue = issueQuery.getIssueByNumber(issue_number);
						}
						
						issues.add(issue);
					}
				}
				
				cache_revision_issues.put(revision, issues);
			}
			
			changedLines.add(new UpdatedLine(date, String.valueOf(revision), issues, author, line, lineNumber));
		}
	}

	public boolean handleRevision(Date date, long revision, String author, File contents) throws SVNException {
//		System.out.println("date: " + date);
//		System.out.println("revision: " + revision);
//		System.out.println("author: " + author);
//		System.out.println("contents: " + contents.getName());
//		System.out.println("************************");
		return false;
	}

}
