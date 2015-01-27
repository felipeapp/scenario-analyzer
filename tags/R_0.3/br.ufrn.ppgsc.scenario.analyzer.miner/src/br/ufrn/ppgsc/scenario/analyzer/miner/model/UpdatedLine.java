package br.ufrn.ppgsc.scenario.analyzer.miner.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class UpdatedLine {

	private Date date;
	private String revision;
	private List<Issue> issues;
	private String author;
	private String line;
	private int lineNumber;

	public UpdatedLine(Date date, String revision, List<Issue> issues, String author, String line, int lineNumber) {
		this.date = date;
		this.revision = revision;
		this.issues = issues;
		this.author = author;
		this.line = line;
		this.lineNumber = lineNumber;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public List<Issue> getIssues() {
		return Collections.unmodifiableList(issues);
	}

	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

}
