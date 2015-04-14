package br.ufrn.ppgsc.scenario.analyzer.miner.model;

public class MethodLimit {

	private String signature;
	private int startLine;
	private int endLine;

	public MethodLimit(String signature, int startLine, int endLine) {
		this.signature = signature;
		this.startLine = startLine;
		this.endLine = endLine;
	}

	public String getSignature() {
		return signature;
	}

	public int getStartLine() {
		return startLine;
	}

	public int getEndLine() {
		return endLine;
	}

}
