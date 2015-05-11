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
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Commit;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.CommitStat;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.parser.PackageDeclarationParser;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public class GitUpdatedLinesHandler {

	private final Logger logger = Logger.getLogger(GitUpdatedLinesHandler.class);
	
	private static final Map<String, Commit> cache_commits = new HashMap<String, Commit>();
	
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
	
	private String getSourceCodeByCommit(String filepath, String revision) throws IOException {
		String so_prefix = "cmd /c ";
		String command = so_prefix + "git show " + revision + ":" + filepath;
		
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
	
	private String readAndCheckLine(BufferedReader in, String prefix) throws IOException {
		return readAndCheckLine(in, new String[]{prefix});
	}
	
	private String readAndCheckLine(BufferedReader in, String[] prefixes) throws IOException {
		String line = "";
		String msg = "";
		boolean match = false;
		
		// Ignore blank lines
		while (line.isEmpty())
			line = in.readLine();
		
		for (String p : prefixes) {
			msg += p + ",";
			
			if (line.startsWith(p)) {
				match = true;
				break;
			}
		}
		
		if (!match)
			throw new RuntimeException("Prefix(es) " + msg.substring(0, msg.lastIndexOf(',')) + " do(es) not match with " + line);
		
		return line;
	}
	
	private Collection<CommitStat> getCommitStats(String commit) throws IOException {
		String so_prefix = "cmd /c ";
		String command = so_prefix + "git show -C --oneline " + commit;
		
		Process p = Runtime.getRuntime().exec(command, null, new File(filedir));
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		
		Collection<CommitStat> stats = new ArrayList<CommitStat>();
		
		// The first line contains only commit information
		String line = br.readLine();
		
		// Reading the first diff command
		line = readAndCheckLine(br, "diff --");
		
		// It will be null at the end of the file
		while (line != null) {
			// In case of null path in new mode file if below
			String diff_line = line;
			
			// Reading next line. It will indicate the operation (rename, added, deleted)
			line = br.readLine();
			
			// It is not the operation yet
			if (line.startsWith("old mode")) {
				line = readAndCheckLine(br, "new mode"); // Reading new mode line
				line = readAndCheckLine(br, "similarity index"); // Reading operation now (in this case a similarity line)
			}
			
			CommitStat.Operation operation = null;
			String updated_path = null;
			
			// Reading extra lines when it was a rename/copy (similarity line)
			if (line.startsWith("similarity index")) {
				readAndCheckLine(br, new String[]{"rename from", "copy from"});
				String line_to = readAndCheckLine(br, new String[]{"rename to", "copy to"});
				
				line = br.readLine(); // It should be the index line
				
				// This similarity has no diff
				if (line == null || line.startsWith("diff --")) {
					updated_path = line_to.substring(10);
				}
				else {
					readAndCheckLine(br, "---"); // Reading --- line (old path): --- a/old_path
					updated_path = readAndCheckLine(br, "+++").substring(6); // Reading +++ line (new path): +++ b/new_path
				}
				
				if (line_to.startsWith("rename to"))
					operation = CommitStat.Operation.RENAME;
				else
					operation = CommitStat.Operation.ADDED;
			}
			// Reading extra lines when it was an addition
			else if (line.startsWith("new file mode")) {
				readAndCheckLine(br, "index"); // Reading index line
				
				// Sometimes it is null
				if (br.readLine() == null) { // Reading --- line (old path): --- a/old_path
					if (diff_line.startsWith("diff --git"))
						updated_path = diff_line.substring(13, diff_line.lastIndexOf(' '));
					else if (diff_line.startsWith("diff --cc"))
						updated_path = diff_line.substring(10, diff_line.lastIndexOf(' '));
					else // Nunca deveria entrar
						throw new RuntimeException("Invalid diff type.");
				}
				else {
					updated_path = readAndCheckLine(br, "+++").substring(6); // Reading +++ line (new path): +++ b/new_path
				}
				
				operation = CommitStat.Operation.ADDED;
			}
			// Reading extra lines when it was a deletion
			else if (line.startsWith("deleted file mode")) {
				readAndCheckLine(br, "index"); // Reading index line
				updated_path = readAndCheckLine(br, "---").substring(6); // Reading --- line (old path): --- a/old_path
				readAndCheckLine(br, "+++"); // Reading +++ line (new path): +++ b/new_path
				operation = CommitStat.Operation.DELETED;
			}
			// The line variable is already the index line
			else {
				readAndCheckLine(br, "---"); // Reading --- line (old path): --- a/old_path
				updated_path = readAndCheckLine(br, "+++").substring(6); // Reading +++ line (new path): +++ b/new_path
				operation = CommitStat.Operation.MODIFIED;
			}
			
			int insertions = 0;
			int deletions = 0;
			int hunks = 0;
			boolean no_new_line_eof = false;
			
			// It will be null at the end of the file
			// It will be diff command when finishing a set of stats 
			while (line != null && !line.startsWith("diff --")) {
				// Counting
				if (line.equals("\\ No newline at end of file"))
					no_new_line_eof = true;
				else if (line.startsWith("+") || line.startsWith(" +"))
					++insertions;
				else if (line.startsWith("-") || line.startsWith("-"))
					++deletions;
				else if (line.startsWith("@@"))
					++hunks;
				
				// Reading a source code line
				line = br.readLine();
			}
			
			// Correcting the issue when there is no new line at the end of the file
			if (no_new_line_eof)
				if (operation == CommitStat.Operation.ADDED)
					deletions = 0;
				else if (operation == CommitStat.Operation.DELETED)
					insertions = 0;
			
			// Probably a no-text file
			if (hunks == 0)
				insertions = deletions = hunks = 0;
			
			String package_name = null;
			
			// Getting the package name if it is a java file
			if (updated_path.endsWith(".java")) {
				PackageDeclarationParser parse = new PackageDeclarationParser(getSourceCodeByCommit(updated_path, commit));
				package_name = parse.getPackageName();
			}
			
			// Adding the commit stat to the result list
			stats.add(new CommitStat(updated_path, package_name, insertions, deletions, hunks, operation));
		}
		
		return stats;
	}

	private UpdatedLine handleLine(String gitblameline) throws IOException {
		Scanner in = new Scanner(gitblameline);
		
		String commit_revision = in.next();
		
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
		String commit_tz = null;
		try {
			commit_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
					tokens.get(tokens.size() - 4) + " " + tokens.get(tokens.size() - 3));
			commit_tz = tokens.get(tokens.size() - 2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String author_name = "";
		for (int i = 0; i < tokens.size() - 4; i++)
			author_name += (i == 0 ? "" : " ") + tokens.get(i);
		
		sourceCode.append(source_line + System.lineSeparator());
		
		// Commits fora do período dos releases são desconsiderados
		if (!commit_revision.startsWith("^")) {
			Commit commit = cache_commits.get(commit_revision);
			
			if (commit == null) {
				String path = filedir + filename;
				
				logger.info("\tGetting issues to " + commit_revision + " in " + path);
				
				String logMessage = getCommitLogMessage(commit_revision);
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
				
				/*
				 *  TODO: Refazer a parte da data e autor junto com os stats usando git show -shortstat <commit>.
				 *  O parse do resultado será mais complicado, mas será apenas uma requisição remota no lugar de duas.
				 */
				Collection<CommitStat> stats = getCommitStats(commit_revision);
				
				commit = new Commit(commit_revision, author_name, commit_date, commit_tz, issues, stats);
				
				// Cache do commit analisado
				cache_commits.put(commit_revision, commit);
			}
			
			return new UpdatedLine(commit, source_line, line_number);
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
		
//		gitHandler.calculateChangedLines();
//		
//		Collection<UpdatedLine> list = gitHandler.getChangedLines();
//		
//		for (UpdatedLine up_line : list) {
//			System.out.println(up_line.getCommit().getAuthor());
//			System.out.println(up_line.getLine());
//			System.out.println(up_line.getLineNumber());
//			System.out.println(up_line.getCommit().getRevision());
//			System.out.println(up_line.getCommit().getDate());
//			
//			for (Issue i : up_line.getCommit().getIssues()) {
//				System.out.println("\t" + i.getId());
//				System.out.println("\t" + i.getNumber());
//				System.out.println("\t" + i.getType());
//			}
//			
//			System.out.println("------------------------------");
//		}
		
		System.out.println("####################");
		//System.out.println(gitHandler.getSourceCode());
		System.out.println("####################");
		
		// 3ecbe996ed8adc6f20fc42e5e50e0f189bc2c128 -> com mudança de diretório
		// 0bd6020eb5e1d3c029075e6a78efa6c16040cef8 -> com renomeação e adição
		// cb5da57c846bb24a291df2d864fa0ea7d3298015 -> com remoção
		// 3b288e37ca3b2ebb10e6c9ba4b5e869411cc17a4 -> estranha situação, apenas teste
		// e4262674d6dd347fb51a1454c63e5f03ed5f135e -> Outra situação estranha, apenas teste
		// 95c2579ace678903b2e63023ced97be4877b6889 -> Usa a palavra copy no lugar de rename
		// d06f84d1b87011e5c152c5fb3f05ae50c1c58cda -> Situação estranha, no new line at the end of the file
		// 7e032d211feecf00b93f72fd0ee49c42abf08c61 -> Usa diff --cc
		Collection<CommitStat> stats = gitHandler.getCommitStats("7e032d211feecf00b93f72fd0ee49c42abf08c61");
		
		Commit commit = new Commit(null, null, null, null, null, stats);
		
		System.out.println(commit.getStats().size());
		System.out.println(commit.getPackages().size());
		System.out.println(commit.getNumberOfInsertions());
		System.out.println(commit.getNumberOfDeletions());
		System.out.println(commit.getNumberOfHunks());
		
		int i = 0;
		for (CommitStat s: stats) {
			System.out.println(++i + " - " + s.getPackageName() + ", " + s.getInsertions() + ", " + s.getDeletions()
					+ ", " + s.getHunks() + ", " + s.getOperation() + ", " + s.getPath());
		}
	}

}
