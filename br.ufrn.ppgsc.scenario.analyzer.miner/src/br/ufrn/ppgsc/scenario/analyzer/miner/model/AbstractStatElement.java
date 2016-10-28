package br.ufrn.ppgsc.scenario.analyzer.miner.model;

public abstract class AbstractStatElement {

	// Element name (scenario name or method name)
	private String elementName;

	public AbstractStatElement(String elementName) {
		super();
		this.elementName = elementName;
	}

	public String getElementName() {
		return elementName;
	}

}
