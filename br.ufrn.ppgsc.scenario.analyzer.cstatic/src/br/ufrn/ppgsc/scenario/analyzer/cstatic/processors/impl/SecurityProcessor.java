package br.ufrn.ppgsc.scenario.analyzer.cstatic.processors.impl;

import java.lang.annotation.Annotation;

import br.ufrn.ppgsc.scenario.analyzer.common.annotations.Security;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.AbstractQAData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.processors.AbstractProcessorQA;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.util.ScenarioAnalyzerUtil;

public class SecurityProcessor extends AbstractProcessorQA {

	public AbstractQAData createInstance() {
		return ScenarioAnalyzerUtil.getFactoryDataElement().createSecurityData();
	}

	public Class<? extends Annotation> getAnnotationClass() {
		return Security.class;
	}

}
