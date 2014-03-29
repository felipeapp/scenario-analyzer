package br.ufrn.ppgsc.scenario.analyzer.cstatic.processors.impl;

import java.lang.annotation.Annotation;

import br.ufrn.ppgsc.scenario.analyzer.common.annotations.Performance;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.AbstractQAData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.processors.AbstractProcessorQA;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.util.ScenarioAnalyzerUtil;

public class PerformanceProcessor extends AbstractProcessorQA {

	public AbstractQAData createInstance() {
		return ScenarioAnalyzerUtil.getFactoryDataElement().createPerformanceData();
	}

	public Class<? extends Annotation> getAnnotationClass() {
		return Performance.class;
	}

}
