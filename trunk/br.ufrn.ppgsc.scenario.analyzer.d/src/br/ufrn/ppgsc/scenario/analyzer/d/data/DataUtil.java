package br.ufrn.ppgsc.scenario.analyzer.d.data;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import br.ufrn.ppgsc.scenario.analyzer.annotations.Performance;
import br.ufrn.ppgsc.scenario.analyzer.annotations.Reliability;
import br.ufrn.ppgsc.scenario.analyzer.annotations.Robustness;
import br.ufrn.ppgsc.scenario.analyzer.annotations.Security;

public abstract class DataUtil {
	
	public static void printScenarioTree(RuntimeCallGraph tree, Appendable buffer) throws IOException {
		buffer.append("Scenario: " + tree.getScenarioName() + " (ID: " + tree.getThreadId() + ")\n   ");
		printInOrder(tree.getRoot(), buffer);
		buffer.append(System.lineSeparator());
		printTreeNode(tree.getRoot(), "   ", buffer);
	}
	
	private static void printInOrder(RuntimeNode root, Appendable buffer) throws IOException {
		buffer.append(root.getMember().getName());
		
		for (RuntimeNode node : root.getChildren()) {
			buffer.append(" > ");
			printInOrder(node, buffer);
		}
	}
	
	private static void printTreeNode(RuntimeNode root, String tabs, Appendable buffer) throws IOException {
		buffer.append(tabs + root.getMember().getName() + " (" + root.getTime() + "ms, "
				+ (root.getException() == null ? false : true) + ")");
		
		AnnotatedElement element = (AnnotatedElement) root.getMember();
		
		Annotation annotation = element.getAnnotation(Performance.class);
		if (annotation != null)
			buffer.append(" [Performance: " + ((Performance) annotation).name() + "]");
		
		annotation = element.getAnnotation(Security.class);
		if (annotation != null)
			buffer.append(" [Security: " + ((Security) annotation).name() + "]");
		
		annotation = element.getAnnotation(Reliability.class);
		if (annotation != null)
			buffer.append(" [Reliability: " + ((Reliability) annotation).name() + "]");
		
		annotation = element.getAnnotation(Robustness.class);
		if (annotation != null)
			buffer.append(" [Robustness: " + ((Robustness) annotation).name() + "]");
		
		buffer.append(System.lineSeparator());
		
		for (RuntimeNode node : root.getChildren()) {
			printTreeNode(node, tabs + "   ", buffer);
		}
	}

}
