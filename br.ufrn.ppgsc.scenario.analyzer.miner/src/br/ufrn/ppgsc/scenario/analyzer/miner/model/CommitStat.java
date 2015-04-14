package br.ufrn.ppgsc.scenario.analyzer.miner.model;

public class CommitStat {

	private String path;
	private String package_name;
	private int insertions;
	private int deletions;

	public CommitStat(String path, String package_name, int insertions, int deletions) {
		this.path = path;
		this.package_name = package_name;
		this.insertions = insertions;
		this.deletions = deletions;
	}

	public String getPath() {
		return path;
	}

	public String getPackageName() {
		return package_name;
	}

	public int getInsertions() {
		return insertions;
	}

	public int getDeletions() {
		return deletions;
	}

}
