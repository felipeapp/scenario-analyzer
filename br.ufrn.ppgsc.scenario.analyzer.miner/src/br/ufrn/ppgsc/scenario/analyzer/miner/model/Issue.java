package br.ufrn.ppgsc.scenario.analyzer.miner.model;

import java.util.Date;

public class Issue {

	private long issueId;
	private long number;
	private String issueStatus;
	private String component;
	private String affectedVersion;
	private Date dateCreation;
	private String issueType;
	private long issueTypeId;
	private String shortDescription;

	public Issue() {
		issueId = number = 0;
		issueType = "No issue (0)";
	}

	public long getIssueId() {
		return issueId;
	}

	public void setIssueId(long issueId) {
		this.issueId = issueId;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public String getIssueStatus() {
		return issueStatus;
	}

	public void setIssueStatus(String issueStatus) {
		this.issueStatus = issueStatus;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getAffectedVersion() {
		return affectedVersion;
	}

	public void setAffectedVersion(String affectedVersion) {
		this.affectedVersion = affectedVersion;
	}

	public Date getDateCreation() {
		return dateCreation;
	}

	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}

	public String getIssueType() {
		return issueType;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	public long getIssueTypeId() {
		return issueTypeId;
	}

	public void setIssueTypeId(long issueTypeId) {
		this.issueTypeId = issueTypeId;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Issue && number == ((Issue) obj).getNumber();
	}

}