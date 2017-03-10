package br.ufrn.ppgsc.scenario.analyzer.miner.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;

import br.ufrn.ppgsc.scenario.analyzer.miner.ifaces.IQueryIssue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Commit;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.CommitStat;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.Issue;
import br.ufrn.ppgsc.scenario.analyzer.miner.model.UpdatedLine;
import br.ufrn.ppgsc.scenario.analyzer.miner.parser.PackageDeclarationParser;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.AnalyzerCollectionUtil;
import br.ufrn.ppgsc.scenario.analyzer.miner.util.SystemMetadataUtil;

public class GitUpdatedLinesHandler {

	private final Logger logger = Logger.getLogger(GitUpdatedLinesHandler.class);
	
	private static final Map<String, Commit> cache_commits = new HashMap<String, Commit>();
	private static final Map<String, Collection<String>> release_to_commits = new LinkedHashMap<String, Collection<String>>();
	
	// Using format yyyy-MM-dd HH:mm:ss Z
	private static final Map<String, String> release_to_dates = new LinkedHashMap<String, String>();
	
	private List<UpdatedLine> changedLines;
	private StringBuilder sourceCode;
	private IQueryIssue issueQuery;
	
	private String startRev;
	private String endRev;
	private String gitdir;
	private String filename;
	
	public GitUpdatedLinesHandler(String startRev, String endRev, String gitdir, String filename, String[] releases) {
		changedLines = new ArrayList<UpdatedLine>();
		sourceCode = new StringBuilder();
		issueQuery = SystemMetadataUtil.getInstance().newObjectFromProperties(IQueryIssue.class);
		
		this.startRev = startRev;
		this.endRev = endRev;
		this.gitdir = gitdir;
		this.filename = filename;
		
		if (release_to_commits.isEmpty() || release_to_dates.isEmpty()) {
			// It should never happen here
			if (!release_to_commits.isEmpty() || !release_to_dates.isEmpty())
				throw new RuntimeException("Maps should be both empty!");
			
			try {
				loadCommitsOfReleases(releases);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
//		for (String release : release_to_commits.keySet()) {
//			Collection<String> commits = release_to_commits.get(release);
//			
//			System.out.println(release + " - " + commits.size());
//			
//			for (String commit : commits)
//				System.out.println("\t" + commit);
//		}
	}
	
	private void loadCommitsOfReleases(String[] releases) throws IOException {
		for (int i = 0; i < releases.length - 1; i++) {
			Collection<String> commits = new ArrayList<String>();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(executeGitCommand(
					"git log " + releases[i] + ".." + releases[i + 1] + " --format=%H")));
			
			String line = null;
			while ((line = br.readLine()) != null)
				commits.add(line);
			
			br.close();
			
			br = new BufferedReader(new InputStreamReader(executeGitCommand(
					"git log " + releases[i + 1] + " -n 1 --format=%ai")));
			
			release_to_commits.put(releases[i + 1], commits);
			release_to_dates.put(releases[i + 1], br.readLine());
			
			br.close();
		}
	}
	
	public List<UpdatedLine> getChangedLines() {
		return Collections.unmodifiableList(changedLines);
	}
	
	public String getSourceCode() {
		return sourceCode.toString();
	}
	
	public void calculateChangedLines() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					executeGitCommand("git blame -l " + startRev + ".." + endRev + " " + filename)));
			
			String line = null;
			while ((line = br.readLine()) != null) {
				UpdatedLine up = handleLine(line);
				
				if (up != null)
					changedLines.add(up);
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private String getCommitLogMessage(String commit) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(executeGitCommand("git log --format=%B -n 1 " + commit)));
		StringBuilder sb = new StringBuilder();
		
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append(System.lineSeparator());
		}
		
		br.close();
		return sb.toString();
	}
	
	private String getSourceCodeByCommit(String filepath, String revision, CommitStat.Operation operation) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(executeGitCommand("git show " + revision + ":" + filepath)));
		StringBuilder sb = new StringBuilder();
		
		String line = null;
		while ((line = br.readLine()) != null) {
			sb.append(line);
			sb.append(System.lineSeparator());
		}
		
		br.close();
		
		String source_code = sb.toString();
		
		// It should never happen
		if (operation != CommitStat.Operation.DELETED && (source_code.isEmpty() || source_code.startsWith("fatal:")))
			throw new RuntimeException(revision + ", Impossible to download source code of " + filepath + "\n" + source_code + "\n" + operation);
		
		return source_code;
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

	private InputStream executeGitCommand(String command) throws IOException {
		String torun = "cmd /c " + command;
		//System.out.println(torun);
		Process p = Runtime.getRuntime().exec(torun, null, new File(gitdir));
		return p.getInputStream();
	}
	
	private int getNumberOfParents(String commit) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(executeGitCommand("git show --format=\"%P\" " + commit)));
		String line = br.readLine();
		br.close();
		
		int n = line.split(" ").length;
		
		if (n <= 0 || line.startsWith("fatal:"))
			throw new RuntimeException("Invalid number of parents: " + commit + " --> " + line);
		
		return n;
	}
	
	private Collection<CommitStat> getCommitStats(String commit) throws IOException {
		Set<CommitStat> stats = null;
		int number_of_parents = getNumberOfParents(commit);

		if (number_of_parents > 1)
			stats = getCommitStatsMerge("git show -c -C100% --oneline", commit, "--combined");
		else
			stats = new HashSet<CommitStat>();
		
		stats.addAll(getCommitStatsMerge("git show -m -C100% --oneline", commit, "--git"));
		
		return stats;
	}
	
	private Set<CommitStat> getCommitStatsMerge(String command, String commit, String flag) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(executeGitCommand(command + " " + commit)));
		
		Set<CommitStat> stats = new HashSet<CommitStat>();
		
		// The first line contains only commit information
		String line = readAndCheckLine(br, commit.substring(0, 7));
		
		// Reading the first diff command
		line = readAndCheckLine(br, "diff " + flag);
		
		// It will be null at the end of the file
		while (line != null) {
			// First we calculate a backup of path from diff line
			String path_from_diff = null;
			
			// Testing the flag
			if (flag.equals("--git"))
				path_from_diff = line.substring(8 + "--git".length(), line.lastIndexOf(' '));
			else if (flag.equals("--combined"))
				path_from_diff = line.substring(6 + "--combined".length());
			
			// It should never happen
			if (path_from_diff == null || !line.startsWith("diff " + flag))
				throw new RuntimeException("Invalid diff line in " + commit + ":" + line);
			
			// Reading next line. It will indicate the operation (rename, added, deleted)
			line = br.readLine();
			
			// In this case, it is not the operation yet
			if (line.startsWith("old mode")) {
				line = readAndCheckLine(br, "new mode"); // Reading new mode line
				line = br.readLine(); // Reading operation now
			}
			
			CommitStat.Operation operation = null;
			String updated_path = null;
			
			// Deal with commit when it was a rename/copy (similarity line)
			if (line.startsWith("similarity index")) {
				readAndCheckLine(br, new String[]{"rename from", "copy from"}); // rename or copy (from line)
				String line_to = readAndCheckLine(br, new String[]{"rename to", "copy to"}); // rename or copy (to line)
				
				line = br.readLine(); // It should be the index line
				
				/*
				 * If it was not the index line, we do not have a diff comparison.
				 * In this case we are at the end of the file (null) or in a new diff.
				 */
				if (line == null || line.startsWith("diff " + flag)) {
					updated_path = line_to.substring(line_to.indexOf("to") + 3); // Getting the updated path from the "to line" (after the first to word) 
				}
				else if (line.startsWith("index")) {
					readAndCheckLine(br, "---"); // Reading --- line (old path): --- a/old_path
					updated_path = readAndCheckLine(br, "+++").substring(6); // Reading +++ line (new path): +++ b/new_path
				}
				else { // It should never happen
					throw new RuntimeException("Invalid similarity index format: " + line);
				}
				
				operation = CommitStat.Operation.RENAME;
			}
			// Reading extra lines when it was an addition
			else if (line.startsWith("new file mode")) {
				readAndCheckLine(br, "index"); // Reading index line
				
				/*
				 * end of the file (null)
				 * --- line;
				 * binary line
				 */
				line = br.readLine();
				
				/*
				 * If it was not the "--- line", we do not have a diff comparison.
				 * In this case, we are at the end of the file (null),
				 * or we are in a binary line.
				 */
				if (line == null || line.startsWith("Binary files"))
					updated_path = path_from_diff; // Getting the updated path from the "diff line"
				else if (line.startsWith("---"))
					updated_path = readAndCheckLine(br, "+++").substring(6); // Reading +++ line (new path): +++ b/new_path
				else // It should never happen
					throw new RuntimeException("Invalid new file mode: " + line);
				
				operation = CommitStat.Operation.ADDED;
			}
			// Reading extra lines when it was a deletion
			else if (line.startsWith("deleted file mode")) {
				readAndCheckLine(br, "index"); // Reading index line
				
				// It should be a "--- line" or a "binary line"
				line = readAndCheckLine(br, new String[]{"---", "Binary files"}); 
				
				/*
				 * If it was not the "--- line", we do not have a diff comparison.
				 * In this case, we are at the end of the file (null),
				 * or we are in a binary line.
				 */
				if (line == null || line.startsWith("Binary files")) {
					updated_path = path_from_diff; // Getting the updated path from the "diif line"
				}
				else if (line.startsWith("---")) {
					updated_path = line.substring(6); // Reading --- line (old path): --- a/old_path
					readAndCheckLine(br, "+++"); // Reading +++ line (new path): +++ b/new_path
				}
				else { // It should never happen
					throw new RuntimeException("Invalid deleted file mode: " + line);
				}
				
				operation = CommitStat.Operation.DELETED;
			}
			// The line variable is already the index line
			else if (line.startsWith("index")) {
				// It should be a "--- line", "binary line" or mode
				line = readAndCheckLine(br, new String[]{"---", "Binary files", "mode"});
				
				/*
				 * If it was not the "--- line", we do not have a diff comparison.
				 * In this case, we are at the end of the file (null),
				 * or we are in a binary line.
				 */
				if (line == null || line.startsWith("Binary files")) {
					updated_path = path_from_diff; // Getting the updated path from the "diif line"
				}
				else if (line.startsWith("---")) {
					updated_path = readAndCheckLine(br, "+++").substring(6); // Reading +++ line (new path): +++ b/new_path
				}
				else if (line.startsWith("mode")) {
					readAndCheckLine(br, "---"); // Reading --- line (old path): --- a/old_path
					updated_path = readAndCheckLine(br, "+++").substring(6); // Reading +++ line (new path): +++ b/new_path
				}
				else { // It should never happen
					throw new RuntimeException("Invalid index mode: " + line);
				}
				
				operation = CommitStat.Operation.MODIFIED;
			}
			// It should never happen
			else {
				throw new RuntimeException("Invalid operation: " + line);
			}
			
			int insertions = 0;
			int deletions = 0;
			int hunks = 0;
			String package_name = null;
			
			boolean binary = line != null && line.startsWith("Binary files");
			
			// Value to start the loop
			if (line == null || !line.startsWith("diff " + flag))
				line = "";
			
			/*
			 * It will be null at the end of the file
			 * It will be diff command when finishing a set of stats
			 */
			while (line != null && !line.startsWith("diff " + flag)) {
				// Counting
				if (line.startsWith("+"))
					++insertions;
				else if (line.startsWith("-"))
					++deletions;
				else if (line.startsWith("@@"))
					++hunks;
				
				// Reading a source code line
				line = br.readLine();
			}
			
			// It should never happen
			if (operation == CommitStat.Operation.ADDED && deletions != 0 ||
					operation == CommitStat.Operation.DELETED && insertions != 0 ||
					hunks == 0 && (insertions != 0 || deletions != 0))
				throw new RuntimeException("Wrong number of lines for " + commit + ":" + updated_path);
			
			// Getting the package name if it is a java file
			if (updated_path.endsWith(".java")) {
				PackageDeclarationParser parse = new PackageDeclarationParser(getSourceCodeByCommit(updated_path, commit, operation));
				package_name = parse.getPackageName();
			}
			
			// Adding the commit stat to the result list
			stats.add(new CommitStat(updated_path, package_name, insertions, deletions, hunks, operation, binary));
		}
		
		br.close();
		
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
		
		// Using format yyyy-MM-dd HH:mm:ss
		String string_date = tokens.get(tokens.size() - 4) + " " + tokens.get(tokens.size() - 3);
		
		Date commit_date = null;
		String commit_tz = null;
		try {
			commit_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(string_date);
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
				logger.info("\tGetting issues to " + commit_revision + " in " + filename);
				
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
				 *  O parse do resultado será mais complicado, mas será apenas uma requisição no lugar de duas.
				 */
				Collection<CommitStat> stats = null;//getCommitStats(commit_revision);
				
				commit = new Commit(commit_revision, author_name, commit_date, commit_tz, issues, stats,
						getAuthorCommitsBeforeDate(author_name, string_date + " " + commit_tz).size(),
						AnalyzerCollectionUtil.diffDays(string_date + " " + commit_tz, getDateOfNextRelease(commit_revision)));
				
				// Cache do commit analisado
				cache_commits.put(commit_revision, commit);
			}
			
			return new UpdatedLine(commit, source_line, line_number);
		}
		
		return null;
	}

	private Collection<String> getAuthorCommitsBeforeDate(String author, String date) throws IOException {
		String command = "git log --author=\"" + author + "\" --before=\"" + date + "\" --format=%H";
		
		BufferedReader br = new BufferedReader(new InputStreamReader(executeGitCommand(command)));
		
		Collection<String> result = new ArrayList<String>();
		
		String line = null;
		while ((line = br.readLine()) != null)
			result.add(line);
		
		br.close();
		
		return result;
	}
	
	// Using format yyyy-MM-dd HH:mm:ss Z
	private String getDateOfNextRelease(String current_commit) {
		for (String release : release_to_commits.keySet())
			if (release_to_commits.get(release).contains(current_commit))
				return release_to_dates.get(release);
		
		// It should never be here
		throw new RuntimeException("Can't find commit of next release!");
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(" +".startsWith("+"));
		
		String line = "copy to wicket-cdi/src/main/resources/META-INF/beans.xml";
		System.out.println(line.substring(line.indexOf("to") + 3));
		
		line = "rename to wicket-examples/src/main/java/org/apache/wicket/examples/niceurl/Page2UP.html";
		System.out.println(line.substring(line.indexOf("to") + 3));
		
		System.out.println("+++ b/wicket-examples/src/main/java/org/apache/wicket/examples/niceurl/Page2UP.java".substring(6));
		
		String line_git = "diff --git a/wicket-core/src/test/java/org/apache/wicket/markup/html/border/BoxBorderTestPage_ExpectedResult_10.html b/wicket-experimental/wicket-cdi-1.1/wicket-cdi-1.1-core/src/main/resources/META-INF/beans.xml";
		String line_c = "diff --combined wicket-core/src/main/java/org/apache/wicket/core/request/handler/PageProvider.java";
		System.out.println("A: " + line_git.substring(8 + "--git".length(), line_git.lastIndexOf(' ')));
		System.out.println("B: " + line_c.substring(6 + "--combined".length()));
		
//		GitUpdatedLinesHandler gitHandler = new GitUpdatedLinesHandler(
//				"c5d8af446a39db10a1744d47e5a466fa1c87a374",
//				"b562148e2d8d0f0487495fb5dd2d5de62306c5e0",
//				"C:/Users/Felipe/git/netty/transport/src/main/java/io/netty/channel/",
//				"DefaultChannelHandlerContext.java");
		
		GitUpdatedLinesHandler gitHandler = new GitUpdatedLinesHandler(
				"7f0dc74db4ed30e2831c439d10fe0244813bce3e",
				"021348160abf428bee0be2eca770cd08142ad168",
				"C:/Users/Felipe/git/wicket",
				"wicket-core/src/main/java/org/apache/wicket/MarkupContainer.java",
				new GitUpdatedMethodsMiner().getReleases());
		
		Collection<String> hashes = gitHandler.getAuthorCommitsBeforeDate("Martijn Dashorst", "2014-04-20 20:00:00");
		System.out.println("hashes size: " + hashes.size());
		int ihash = 0;
		for (String hash : hashes)
			System.out.println(++ihash + " - " + hash);
		
		gitHandler.calculateChangedLines();
		
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
//		System.out.println(gitHandler.getSourceCode());
		System.out.println("####################");
		
		// 3ecbe996ed8adc6f20fc42e5e50e0f189bc2c128 -> com mudança de diretório
		// 0bd6020eb5e1d3c029075e6a78efa6c16040cef8 -> com renomeação e adição
		// cb5da57c846bb24a291df2d864fa0ea7d3298015 -> com remoção
		// 3b288e37ca3b2ebb10e6c9ba4b5e869411cc17a4 -> estranha situação, apenas teste
		// e4262674d6dd347fb51a1454c63e5f03ed5f135e -> Outra situação estranha, apenas teste
		// ed64e166dcba6715eafcbb7ca460d2b87e84cffc -> old mode and new mode with index
		// 95c2579ace678903b2e63023ced97be4877b6889 -> Usa a palavra copy no lugar de rename
		// d06f84d1b87011e5c152c5fb3f05ae50c1c58cda -> Situação estranha, no new line at the end of the file
		// 22f1e048923cf5b6e020a81b66e0a8512c24fe79 -> mode line que não pensei
		// 7e032d211feecf00b93f72fd0ee49c42abf08c61 -> Commit merge gigante, tem todos os casos, testar este principalmente
		Collection<CommitStat> stats = gitHandler.getCommitStats("dc5edcac743fc006515fe8f5b283631f6f74935d");
		
		Commit commit = new Commit(null, null, null, null, null, stats, 0, 0);
		
		System.out.println("Files: " + commit.getStats().size());
		System.out.println("Java: " + commit.getNumberOfJavaFiles());
		System.out.println("Packages: " + commit.getPackages().size());
		System.out.println("Insertions:" + commit.getNumberOfInsertions());
		System.out.println("Deletions: " + commit.getNumberOfDeletions());
		System.out.println("Hunks: " + commit.getNumberOfHunks());
		
		int i = 0;
		for (CommitStat s: stats) {
			System.out.println(++i + " - " + s.getPackageName() + ", +" + (s.isBinary()? "-" : s.getInsertions()) + ", -" + s.getDeletions()
					+ ", " + s.getHunks() + ", " + s.getOperation() + ", " + s.getPath());
		}
		
//		for (CommitStat s : stats)
//			System.out.printf("%d\t%d\t%s%s", s.getInsertions(), s.getDeletions(), s.getPath(), System.getProperty("line.separator"));
	}

}
