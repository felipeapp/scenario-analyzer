package br.ufrn.ppgsc.scenario.analyzer.cstatic.visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;

import br.ufrn.ppgsc.scenario.analyzer.cstatic.util.JDTWALADataStructure;

public class AnnotationVisitor extends ASTVisitor {

	private JDTWALADataStructure data;

	public AnnotationVisitor(JDTWALADataStructure data) {
		this.data = data;
	}

	public boolean visit(SingleMemberAnnotation node) {
		data.addAnnotationToIndex(node.getTypeName().getFullyQualifiedName(), node);
		return true;
	}
	
	public boolean visit(NormalAnnotation node) {
		data.addAnnotationToIndex(node.getTypeName().getFullyQualifiedName(), node);
		return true;
	}

	public boolean visit(MarkerAnnotation node) {
		data.addAnnotationToIndex(node.getTypeName().getFullyQualifiedName(), node);
		return true;
	}

}
