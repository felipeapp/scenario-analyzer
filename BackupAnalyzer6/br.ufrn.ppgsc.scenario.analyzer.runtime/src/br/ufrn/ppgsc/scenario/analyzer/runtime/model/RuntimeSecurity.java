package br.ufrn.ppgsc.scenario.analyzer.runtime.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("security")
public class RuntimeSecurity extends RuntimeGenericAnnotation {

	public RuntimeSecurity() {

	}

	public RuntimeSecurity(String name) {
		super(name);
	}

}
