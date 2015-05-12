package br.ufrn.ppgsc.scenario.analyzer.miner.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.TimeZone;

public class Commit {

	private String revision;
	private Date date;
	private String timezone;
	private String author;

	private Collection<String> packages;
	private Collection<Issue> issues;
	private Collection<CommitStat> stats;

	private int insertions;
	private int deletions;
	private int hunks;
	private int java;

	private boolean bug;

	public Commit(String revision, String author, Date date, String timezone, Collection<Issue> issues, Collection<CommitStat> stats) {
		this.revision = revision;
		this.author = author;
		this.date = date;
		this.timezone = timezone;
		this.issues = issues;
		this.stats = stats;

		this.packages = new HashSet<String>();
		this.insertions = this.deletions = this.hunks = this.java = 0;

		this.bug = false;
		
		if (stats == null) {
			this.stats = new ArrayList<CommitStat>();
		} else {
			for (CommitStat s : stats) {
				if (s.getPackageName() != null)
					this.packages.add(s.getPackageName());

				if (s.getPath().endsWith(".java"))
					++this.java;
				
				this.insertions += s.getInsertions();
				this.deletions += s.getDeletions();
				this.hunks += s.getHunks();
			}
		}

		if (issues == null) {
			this.issues = new ArrayList<Issue>();
		} else {
			for (Issue i : issues) {
				if (i.isBugFixing()) {
					this.bug = true;
					break;
				}
			}
		}
	}

	public String getRevision() {
		return revision;
	}

	public Date getDate() {
		return date;
	}

	public String getTimezone() {
		return timezone;
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

	public int getNumberOfHunks() {
		return hunks;
	}

	public boolean isBugFixing() {
		return bug;
	}
	
	public String getFormatedDate() {
		DateFormat formatter = new SimpleDateFormat("EEE dd-MMM-yyyy HH:mm:ss");
		
		if (timezone.equals("UTC"))
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		return formatter.format(date) + " " + timezone;
	}
	
	public int getDateProperty(int property) {
		Calendar c = new GregorianCalendar();
		
		c.setTime(date);
		
		if (timezone.equals("UTC"))
			c.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		return c.get(property);
	}
	
	public int getNumberOfChurns() {
		return insertions + deletions;
	}
	
	public int getDeltaOfLines() {
		return insertions - deletions;
	}
	
	public int getNumberOfJavaFiles() {
		return java;
	}

	@Override
	public int hashCode() {
		return revision.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Commit && revision.equals(((Commit) obj).getRevision());
	}

}
