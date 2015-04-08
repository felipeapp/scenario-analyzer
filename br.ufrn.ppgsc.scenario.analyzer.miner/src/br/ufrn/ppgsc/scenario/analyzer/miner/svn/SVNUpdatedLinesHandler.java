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
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Commit;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public class SVNUpdatedLinesHandler implements ISVNAnnotateHandler {

	private final Logger logger = Logger.getLogger(SVNUpdatedLinesHandler.class);
	
	private static final Map<Long, Commit> cache_revisions = new HashMap<Long, Commit>();
	
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
			Commit commit = cache_revisions.get(revision);
			
			if (commit == null) {
				logger.info("\tGetting issues to revision " + revision + " in " + path);

				String logMessage = repository.getRevisionPropertyValue(revision, SVNRevisionProperty.LOG).getString();
				Collection<Long> issue_numbers = issueQuery.getIssueNumbersFromMessageLog(logMessage);
				Collection<Issue> issues = new ArrayList<Issue>();
				
				if (issue_numbers.isEmpty()) {
					logger.warn("\t[Empty] No issues for log message: " + logMessage);
					issues.add(new Issue());
				}
				else {
					for (Long issue_number : issue_numbers) {
						Issue issue = null;
						
						if (issue_number <= 0) {
							logger.warn("\t[Invalid: " + issue_number + "] No issues for log message: " + logMessage);
							issue = new Issue();
						}
						else {
							logger.info("\t[Found: " + issue_number + "] In log message: " + logMessage);
							issue = issueQuery.getIssueByNumber(issue_number);
						}
						
						issues.add(issue);
					}
				}
				
				commit = new Commit(String.valueOf(revision), author, date, issues);
				cache_revisions.put(revision, commit);
			}
			
			changedLines.add(new UpdatedLine(commit, line, lineNumber));
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
