package br.ufrn.ppgsc.scenario.analyzer.miner.model;

public class CommitStat {

	public enum Operation {
		RENAME, ADDED, DELETED, MODIFIED
	}

	private String path;
	private String packageName;
	private int insertions;
	private int deletions;
	private int hunks;
	private boolean binary;
	private Operation operation;

	public CommitStat(String path, String packageName, int insertions, int deletions, int hunks, Operation operation, boolean binary) {
		this.path = path;
		this.packageName = packageName;
		this.insertions = insertions;
		this.deletions = deletions;
		this.hunks = hunks;
		this.operation = operation;
		this.binary = binary;
	}

	public String getPath() {
		return path;
	}

	public String getPackageName() {
		return packageName;
	}

	public Operation getOperation() {
		return operation;
	}

	public int getInsertions() {
		return insertions;
	}

	public int getDeletions() {
		return deletions;
	}

	public int getHunks() {
		return hunks;
	}

	public boolean isBinary() {
		return binary;
	}

	@Override
	public int hashCode() {
		return path.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof CommitStat && path.equals(((CommitStat) obj).getPath());
	}

}
