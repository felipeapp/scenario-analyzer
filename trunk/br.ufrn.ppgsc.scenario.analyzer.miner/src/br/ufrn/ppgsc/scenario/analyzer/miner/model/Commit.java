package br.ufrn.ppgsc.scenario.analyzer.miner.model;

import java.util.Collection;
import java.util.Date;

public class Commit {

	private String revision;
	private Collection<String> files;
	private Collection<String> packages;
	private Collection<String> classes;
	private Collection<Issue> issues;
	private int insertions;
	private int deletions;
	private Date date;
	private String author;

	public Commit() {

	}

	public Commit(String revision, String author, Date date, Collection<Issue> issues) {
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

	public Collection<String> getFiles() {
		return files;
	}

	public void setFiles(Collection<String> files) {
		this.files = files;
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

	public int getInsertions() {
		return insertions;
	}

	public void setInsertions(int insertions) {
		this.insertions = insertions;
	}

	public int getDeletions() {
		return deletions;
	}

	public void setDeletions(int deletions) {
		this.deletions = deletions;
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

}
