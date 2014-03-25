package br.ufrn.ppgsc.scenario.analyzer.miner.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.ufrn.ppgsc.scenario.analyzer.miner.sigaa.SINFOIProjectIssue;

public class UpdatedLine {

	private Date date;
	private long revision;
	private List<SINFOIProjectIssue> tasks;
	private String author;
	private String line;
	private int lineNumber;

	public UpdatedLine(Date date, long revision, List<SINFOIProjectIssue> tasks, String author, String line, int lineNumber) {
		this.date = date;
		this.revision = revision;
		this.tasks = tasks;
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

	public long getRevision() {
		return revision;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

	public List<SINFOIProjectIssue> getTasks() {
		return Collections.unmodifiableList(tasks);
	}

	public void setTasks(List<SINFOIProjectIssue> tasks) {
		this.tasks = tasks;
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
