package br.ufrn.ppgsc.scenario.analyzer.miner.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

	public Set<String> getCommitsWithIssues() {
		Set<String> commits = new HashSet<String>();

		for (UpdatedLine l : lines) {
			StringBuilder sb = new StringBuilder();

			sb.append(l.getCommit().getRevision());

			for (Issue issue : l.getCommit().getIssues()) {
				sb.append(";");
				sb.append(issue.getNumber());
			}

			commits.add(sb.toString());
		}

		return commits;
	}

	@Override
	public String toString() {
		return limit.getSignature();
	}

}
