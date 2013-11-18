package br.ufrn.ppgsc.scenario.analyzer.d.data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("robustness")
public class RuntimeRobustness extends RuntimeGenericAnnotation {

	public RuntimeRobustness() {

	}

	public RuntimeRobustness(String name) {
		super(name);
	}

}
