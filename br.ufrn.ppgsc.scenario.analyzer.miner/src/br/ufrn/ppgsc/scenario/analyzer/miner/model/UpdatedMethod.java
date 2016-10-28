package br.ufrn.ppgsc.scenario.analyzer.miner.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UpdatedMethod {

	private List<UpdatedLine> lines;
	private MethodLimit limit;

	public UpdatedMethod(MethodLimit limit) {
		lines = new ArrayList<UpdatedLine>();
		this.limit = limit;
	}

	public void addUpdatedLine(UpdatedLine line) {
		lines.add(line);
	}

	public List<UpdatedLine> getUpdatedLines() {
		return Collections.unmodifiableList(lines);
	}

	public MethodLimit getMethodLimit() {
		return limit;
	}

	@Override
	public String toString() {
		return limit.getSignature();
	}

}
