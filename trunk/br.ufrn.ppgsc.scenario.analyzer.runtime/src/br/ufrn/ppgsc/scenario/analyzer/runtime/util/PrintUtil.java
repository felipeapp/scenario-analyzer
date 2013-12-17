package br.ufrn.ppgsc.scenario.analyzer.runtime.util;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import br.ufrn.ppgsc.scenario.analyzer.runtime.data.RuntimeGenericAnnotation;
import br.ufrn.ppgsc.scenario.analyzer.runtime.data.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.runtime.data.RuntimeScenario;

public abstract class PrintUtil {
	
	public static void printScenarioTree(RuntimeScenario tree, Appendable buffer) throws IOException {
		buffer.append("Scenario: " + tree.getName() + " (Id: " + tree.getThreadId() + ", Request: ");
		
		if (tree.getContext() != null && !tree.getContext().isEmpty()) {
			Iterator<String> itr = tree.getContext().values().iterator();
			while (itr.hasNext()) {
				buffer.append(itr.next());
				
				if (itr.hasNext())
					buffer.append(", ");
			}
		}
		else {
			buffer.append("-");
		}
		
		buffer.append(")\n");

		printInOrder(tree.getRoot(), buffer);
		buffer.append(System.lineSeparator());
		printTreeNode(tree.getRoot(), "   ", buffer);
	}
	
	private static void printInOrder(RuntimeNode root, Appendable buffer) throws IOException {
		buffer.append(root.getMemberSignature());
		
		for (RuntimeNode node : root.getChildren()) {
			buffer.append(" > ");
			printInOrder(node, buffer);
		}
	}
	
	private static void printTreeNode(RuntimeNode root, String tabs, Appendable buffer) throws IOException {
		buffer.append(tabs + root.getMemberSignature() + " - " + root.getId() +
				" (" + root.getExecutionTime() + "ms, " +
				(root.getExceptionMessage() == null ? false : true) + ") Parent: " + 
				(root.getParent() == null ? "-" : root.getParent().getId()) + " | Scenario: " +
				(root.getScenario() == null ? "-" : root.getScenario().getName()));
		
		Set<RuntimeGenericAnnotation> annotations = root.getAnnotations();
		
		if (annotations != null && !annotations.isEmpty())
			for (RuntimeGenericAnnotation ann : annotations)
				buffer.append(" | " + ann.getClass().getSimpleName() + ": " + ann.getName());
		
		buffer.append(System.lineSeparator());
		
		for (RuntimeNode node : root.getChildren())
			printTreeNode(node, tabs + "   ", buffer);
	}

}