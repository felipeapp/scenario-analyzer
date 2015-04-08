package br.ufrn.ppgsc.scenario.analyzer.miner.model;

import java.util.Collection;
import java.util.Date;

public class Commit {

	private String revision;
	private Date date;
	private String author;
	private Collection<String> packages;
	private Collection<String> classes;
	private Collection<Issue> issues;
	private Collection<CommitStat> stats;

	public Commit() {

	}

	public Commit(String revision, String author, Date date,
			Collection<Issue> issues) {
		this.revision = revision;
		this.author = author;
		this.date = date;
		this.issues = issues;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Collection<String> getPackages() {
		return packages;
	}

	public void setPackages(Collection<String> packages) {
		this.packages = packages;
	}

	public Collection<String> getClasses() {
		return classes;
	}

	public void setClasses(Collection<String> classes) {
		this.classes = classes;
	}

	public Collection<Issue> getIssues() {
		return issues;
	}

	public void setIssues(Collection<Issue> issues) {
		this.issues = issues;
	}

	public Collection<CommitStat> getStats() {
		return stats;
	}

	public void setStats(Collection<CommitStat> stats) {
		this.stats = stats;
	}

}
