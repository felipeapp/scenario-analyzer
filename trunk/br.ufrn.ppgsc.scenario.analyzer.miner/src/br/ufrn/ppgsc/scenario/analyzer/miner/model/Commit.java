package br.ufrn.ppgsc.scenario.analyzer.miner.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

public class Commit {

	private String revision;
	private Date date;
	private String author;

	private Collection<String> packages;
	private Collection<Issue> issues;
	private Collection<CommitStat> stats;

	private int insertions;
	private int deletions;

	public Commit(String revision, String author, Date date, Collection<Issue> issues, Collection<CommitStat> stats) {
		this.revision = revision;
		this.author = author;
		this.date = date;
		this.issues = issues;
		this.stats = stats;

		this.packages = new HashSet<String>();
		this.insertions = this.deletions = 0;

		for (CommitStat s : stats) {
			if (s.getPackageName() != null)
				this.packages.add(s.getPackageName());
			
			this.insertions += s.getInsertions();
			this.deletions += s.getDeletions();
		}
	}

	public String getRevision() {
		return revision;
	}

	public Date getDate() {
		return date;
	}

	public String getAuthor() {
		return author;
	}

	public Collection<String> getPackages() {
		return Collections.unmodifiableCollection(packages);
	}

	public Collection<Issue> getIssues() {
		return Collections.unmodifiableCollection(issues);
	}

	public Collection<CommitStat> getStats() {
		return stats;
	}

	public int getNumberOfInsertions() {
		return insertions;
	}

	public int getNumberOfDeletions() {
		return deletions;
	}

}
