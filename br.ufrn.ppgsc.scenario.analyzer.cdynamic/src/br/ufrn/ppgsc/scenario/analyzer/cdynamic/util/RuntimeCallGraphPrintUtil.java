package br.ufrn.ppgsc.scenario.analyzer.cdynamic.util;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeGenericAnnotation;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeScenario;

public abstract class RuntimeCallGraphPrintUtil {

	public static void printScenarioTree(RuntimeScenario tree, Appendable buffer) throws IOException {
		buffer.append("Scenario: " + tree.getName() + " (ThreadId: " + tree.getThreadId() + ", Request: ");
		buffer.append(tree.getRequestURL());
		buffer.append(tree.getRequestParameters());
		buffer.append(")\n");

		printInOrder(tree.getRoot(), buffer);
		buffer.append(System.getProperty("line.separator"));
		printTreeNode(tree.getRoot(), "   ", buffer);
	}

	public static void logScenarioTree(RuntimeScenario tree, PrintStream ps) throws IOException {
		logTreeNode(tree.getRoot(), "", ps);
	}

	private static void logTreeNode(RuntimeNode root, String tabs, PrintStream ps) throws IOException {
		ps.println(tabs + root.getMemberSignature());
		for (RuntimeNode node : root.getChildren())
			logTreeNode(node, tabs + ">", ps);
	}

	private static void printInOrder(RuntimeNode root, Appendable buffer) throws IOException {
		buffer.append(root.getMemberSignature());

		for (RuntimeNode node : root.getChildren()) {
			buffer.append(" > ");
			printInOrder(node, buffer);
		}
	}

	private static void printTreeNode(RuntimeNode root, String tabs, Appendable buffer) throws IOException {
		buffer.append(tabs + root.getMemberSignature() + " - " + root.getId() + " (" + root.getExecutionTime() + "ms, "
				+ (root.getExecutionTime() == -1 ? true : false) + ") Parent: "
				+ (root.getParent() == null ? "-" : root.getParent().getId()));

		buffer.append(" | Scenarios: ");
		for (int i = 0; i < root.getScenarios().size(); i++) {
			RuntimeScenario rs = root.getScenarios().get(i);

			buffer.append(rs.getName() + " [" + rs.getId() + "]");

			if (i + 1 < root.getScenarios().size())
				buffer.append(", ");
		}

		buffer.append(" | " + (root.getExceptionMessage() == null ? "-" : root.getExceptionMessage()));

		Set<RuntimeGenericAnnotation> annotations = root.getAnnotations();

		if (annotations != null && !annotations.isEmpty())
			for (RuntimeGenericAnnotation ann : annotations)
				buffer.append(" | " + ann.getClass().getSimpleName() + ": " + ann.getName());

		buffer.append(System.getProperty("line.separator"));

		for (RuntimeNode node : root.getChildren())
			printTreeNode(node, tabs + "   ", buffer);
	}

}
