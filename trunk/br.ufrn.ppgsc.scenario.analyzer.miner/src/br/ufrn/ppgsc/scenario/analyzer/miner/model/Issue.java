package br.ufrn.ppgsc.scenario.analyzer.miner.model;

import java.util.Date;

public class Issue {

	private long id;
	private long number;
	private String status;
	private String component;
	private String affectedVersion;
	private Date creationDate;
	private String type;
	private String shortDescription;
	private boolean isBugFixing;

	public Issue() {
		id = number = 0;
		type = "No issue type";
		isBugFixing = false;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public boolean isBugFixing() {
		return isBugFixing;
	}

	public void setBugFixing(boolean isBugFixing) {
		this.isBugFixing = isBugFixing;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Issue && number == ((Issue) obj).getNumber();
	}

}
