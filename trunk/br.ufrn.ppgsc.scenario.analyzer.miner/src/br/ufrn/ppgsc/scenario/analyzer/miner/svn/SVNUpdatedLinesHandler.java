package br.ufrn.ppgsc.scenario.analyzer.miner.svn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNRevisionProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.ISVNAnnotateHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IQueryIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Commit;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.CommitStat;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.parser.PackageDeclarationParser;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public class SVNUpdatedLinesHandler implements ISVNAnnotateHandler {

	private final Logger logger = Logger.getLogger(SVNUpdatedLinesHandler.class);
	
	private static final Map<Long, Commit> cache_revisions = new HashMap<Long, Commit>();
	
	private List<UpdatedLine> changedLines;
	private StringBuilder sourceCode;
	private IQueryIssue issueQuery;
	
	private String path;
	private SVNRepository repository;

	public SVNUpdatedLinesHandler() {

	}
	
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
				
				commit = new Commit(String.valueOf(revision), author, date, "UTC", issues, getCommitStats(revision));
				cache_revisions.put(revision, commit);
			}
			
			changedLines.add(new UpdatedLine(commit, line, lineNumber));
		}
	}

	public boolean handleRevision(Date date, long revision, String author, File contents) throws SVNException {
		return false;
	}
	
	private int[] getNumberOfChangedLines(SVNLogEntryPath entryPath, long revision) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		String rep_url = repository.getLocation().toDecodedString();
		
		String new_url = rep_url + entryPath.getPath();
		long new_revision = revision;
		
		String old_url;
		long old_revision;
		
		if (entryPath.getCopyPath() == null) {
			old_url = new_url;
			old_revision = new_revision - 1;
		}
		else {
			old_url = rep_url + entryPath.getCopyPath();
			old_revision = entryPath.getCopyRevision();
		}
		
		try {
			SVNClientManager.newInstance(null, repository.getAuthenticationManager()).getDiffClient().doDiff(
					SVNURL.parseURIEncoded(old_url),
					SVNRevision.create(old_revision),
					SVNURL.parseURIEncoded(new_url),
					SVNRevision.create(new_revision),
					SVNDepth.INFINITY,
					true,
					baos);
		} catch (SVNException e) {
			e.printStackTrace();
		}
		
		Scanner in = new Scanner(new ByteArrayInputStream(baos.toByteArray()));
		int insertions = 0;
		int deletions = 0;
		int hunks = 0;
		
		while (in.hasNext()) {
			// Elimina espaços em branco no final da linha
			String line = in.nextLine();//.replaceFirst("\\s+$", "");
			
			// Após eliminar os espaços no final da string, desconsidera linhas modificadas em branco
			//if (line.length() > 1) {
				if (line.startsWith("+"))
					++insertions;
				else if (line.startsWith("-"))
					++deletions;
				else if (line.startsWith("@@"))
					++hunks;
			//}
		}
		
		in.close();
		
		if (hunks == 0) // Probably a no-text file
			return new int[]{0, 0, 0};
		
		return new int[]{insertions - 1, deletions - 1, hunks};
	}
	
	private String getFileFromRepository(String filepath, long revision) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			repository.getFile(filepath, revision, null, baos);
		} catch (SVNException e) {
			e.printStackTrace();
		}
		
		return new String(baos.toByteArray());
	}
	
	private Collection<CommitStat> getCommitStats(long revision) throws SVNException {

		Collection<CommitStat> stats = new ArrayList<CommitStat>();
		Collection<?> logEntries = repository.log(new String[] { "" }, null, revision, revision, true, true);

		for (Iterator<?> entries = logEntries.iterator(); entries.hasNext();) {
			SVNLogEntry logEntry = (SVNLogEntry) entries.next();
			Set<String> set_of_copy_path = new HashSet<String>();
			
			for (String key : logEntry.getChangedPaths().keySet()) {
				SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(key);
				
				if (entryPath.getCopyPath() != null)
					set_of_copy_path.add(entryPath.getCopyPath());
			}
			
			for (String key : logEntry.getChangedPaths().keySet()) {
				SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(key);
				
				logger.info("\tGetting stats for " + entryPath.getPath());
				
				int[] counts;
				String package_name = null;
				CommitStat.Operation operation = null;
				
				if (entryPath.getType() == SVNLogEntryPath.TYPE_ADDED) {
					if (entryPath.getCopyPath() == null)
						operation = CommitStat.Operation.ADDED;
					else
						operation = CommitStat.Operation.RENAME;
				}
				else if (entryPath.getType() == SVNLogEntryPath.TYPE_DELETED) {
					operation = CommitStat.Operation.DELETED;
					
					// Actually, the file was renamed. Ignoring because the new name will be considered added. 
					if (set_of_copy_path.contains(entryPath.getPath()))
						continue;
				}
				else {
					operation = CommitStat.Operation.MODIFIED;
				}
				
				if (entryPath.getPath().endsWith(".java") && entryPath.getType() != SVNLogEntryPath.TYPE_DELETED) {
					String source_code = getFileFromRepository(entryPath.getPath(), revision);
					package_name = new PackageDeclarationParser(source_code).getPackageName();
				}
				
				counts = getNumberOfChangedLines(entryPath, revision);
				
				stats.add(new CommitStat(entryPath.getPath(), package_name, counts[0], counts[1], counts[2], operation));
			}
		}
		
		return stats;

	}
	
	public static void main(String[] args) {
		
		SVNUpdatedMethodsMiner miner = new SVNUpdatedMethodsMiner();
		
		miner.connect("http://scenario-analyzer.googlecode.com/svn", "", "");

		SVNUpdatedLinesHandler handler = new SVNUpdatedLinesHandler(miner.getRepository(), null);
		
		// 690 -> com renomeação e adição
		// 683 -> com deleção
		// 687 -> com binário
		Collection<CommitStat> stats = null;
		
		try {
			stats = handler.getCommitStats(683);
		} catch (SVNException e) {
			e.printStackTrace();
		}
		
		Commit commit = new Commit(null, null, null, null, null, stats);
		
		System.out.println(commit.getStats().size());
		System.out.println(commit.getNumberOfInsertions());
		System.out.println(commit.getNumberOfDeletions());
		System.out.println(commit.getNumberOfHunks());
		
		for (CommitStat s: stats)
			System.out.println(s.getInsertions() + ", " + s.getDeletions()
					+ ", " + s.getHunks() + ", " + s.getOperation() + ", " + s.getPath());
		
		miner.close();
		
	}

}
