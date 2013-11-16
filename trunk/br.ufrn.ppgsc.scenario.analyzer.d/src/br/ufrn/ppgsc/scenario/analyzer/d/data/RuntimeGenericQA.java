package br.ufrn.ppgsc.scenario.analyzer.d.data;

public abstract class RuntimeGenericQA {

	private String annotationName;
	private RuntimeNode node;

	public String getAnnotationName() {
		return annotationName;
	}

	public void setAnnotationName(String annotationName) {
		this.annotationName = annotationName;
	}

	public RuntimeNode getNode() {
		return node;
	}

	public void setNode(RuntimeNode node) {
		this.node = node;
	}

}
