package br.ufrn.ppgsc.scenario.analyzer.miner.sigaa;

// TODO: criar uma interface para acesso a issue
public class SINFOIProjectIssue {

	private long id;
	private long number;
	private long idType;
	private String typeName;

	public SINFOIProjectIssue() {
		id = number = idType = -1;
		typeName = "No task (-1)";
	}

	public SINFOIProjectIssue(long id, long number, long idType, String typeName) {
		this.id = id;
		this.number = number;
		this.idType = idType;
		this.typeName = typeName;
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

	public long getIdType() {
		return idType;
	}

	public void setIdType(long idType) {
		this.idType = idType;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

}
