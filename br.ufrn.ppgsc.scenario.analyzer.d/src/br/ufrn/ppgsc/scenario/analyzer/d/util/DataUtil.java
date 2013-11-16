package br.ufrn.ppgsc.scenario.analyzer.d.util;

import java.io.IOException;
import java.util.Iterator;

import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeNode;
import br.ufrn.ppgsc.scenario.analyzer.d.data.RuntimeScenario;

public abstract class DataUtil {
	
	public static void printScenarioTree(RuntimeScenario tree, Appendable buffer) throws IOException {
		buffer.append("Scenario: " + tree.getName() + " (Id: " + tree.getThreadId() + ", Request: ");
		
		if (tree.getContext() != null) {
			Iterator<String> itr = tree.getContext().values().iterator();
			while (itr.hasNext()) {
				buffer.append(itr.next());
				
				if (itr.hasNext())
					buffer.append(", ");
			}
		}
		else {
			buffer.append("null");
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
		
//		AnnotatedElement element = (AnnotatedElement) root.getMember();
//		
//		Annotation annotation = element.getAnnotation(Performance.class);
//		if (annotation != null)
//			buffer.append(" [Performance: " + ((Performance) annotation).name() + "]");
//		
//		annotation = element.getAnnotation(Security.class);
//		if (annotation != null)
//			buffer.append(" [Security: " + ((Security) annotation).name() + "]");
//		
//		annotation = element.getAnnotation(Reliability.class);
//		if (annotation != null)
//			buffer.append(" [Reliability: " + ((Reliability) annotation).name() + "]");
//		
//		annotation = element.getAnnotation(Robustness.class);
//		if (annotation != null)
//			buffer.append(" [Robustness: " + ((Robustness) annotation).name() + "]");
		
		buffer.append(System.lineSeparator());
		
		for (RuntimeNode node : root.getChildren()) {
			printTreeNode(node, tabs + "   ", buffer);
		}
	}

}
