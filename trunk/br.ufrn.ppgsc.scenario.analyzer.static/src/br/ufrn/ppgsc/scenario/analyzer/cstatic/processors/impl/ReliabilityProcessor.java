package br.ufrn.ppgsc.scenario.analyzer.cstatic.processors.impl;

import java.lang.annotation.Annotation;

import br.ufrn.ppgsc.scenario.analyzer.common.annotations.Reliability;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.AbstractQAData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.processors.AbstractProcessorQA;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.util.ScenarioAnalyzerUtil;

public class ReliabilityProcessor extends AbstractProcessorQA {

	public AbstractQAData createInstance() {
		return ScenarioAnalyzerUtil.getFactoryDataElement().createReliabilityData();
	}

	public Class<? extends Annotation> getAnnotationClass() {
		return Reliability.class;
	}

}
