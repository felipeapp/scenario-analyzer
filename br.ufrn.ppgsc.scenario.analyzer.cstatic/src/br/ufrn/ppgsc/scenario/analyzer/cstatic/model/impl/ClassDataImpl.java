package br.ufrn.ppgsc.scenario.analyzer.cstatic.model.impl;

import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ClassData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ComponentData;

public class ClassDataImpl implements ClassData {

	private String name;
	private ComponentData declaringComponent;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ComponentData getDeclaringComponent() {
		return declaringComponent;
	}

	public void setDeclaringComponent(ComponentData declaringComponent) {
		this.declaringComponent = declaringComponent;
	}

}
