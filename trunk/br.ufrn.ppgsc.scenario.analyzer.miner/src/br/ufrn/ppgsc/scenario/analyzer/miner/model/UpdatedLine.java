package br.ufrn.ppgsc.scenario.analyzer.miner.model;

public class UpdatedLine {

	private Commit commit;
	private String line;
	private int lineNumber;

	public UpdatedLine(Commit commit, String line, int lineNumber) {
		this.commit = commit;
		this.line = line;
		this.lineNumber = lineNumber;
	}

	public Commit getCommit() {
		return commit;
	}

	public void setCommit(Commit commit) {
		this.commit = commit;
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
