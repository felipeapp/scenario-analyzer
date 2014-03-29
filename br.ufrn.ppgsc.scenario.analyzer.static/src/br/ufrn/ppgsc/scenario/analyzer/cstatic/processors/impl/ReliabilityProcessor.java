package br.ufrn.ppgsc.scenario.analyzer.cstatic.processors.impl;

import java.lang.annotation.Annotation;

import org.eclipse.jdt.core.dom.IAnnotationBinding;

import br.ufrn.ppgsc.scenario.analyzer.common.annotations.Reliability;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.AbstractQAData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.model.ReliabilityData;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.processors.AbstractProcessorQA;
import br.ufrn.ppgsc.scenario.analyzer.cstatic.util.ScenarioAnalyzerUtil;

public class ReliabilityProcessor extends AbstractProcessorQA {

	public AbstractQAData createInstance() {
		return ScenarioAnalyzerUtil.getFactoryDataElement().createReliabilityData();
	}

	public Class<? extends Annotation> getAnnotationClass() {
		return Reliability.class;
	}
	
	public void setExtraFields(AbstractQAData qa_data, IAnnotationBinding ann_binding) {
		Object failureRate = ScenarioAnalyzerUtil.getAnnotationValue(ann_binding, "failureRate");
		((ReliabilityData) qa_data).setFailureRate((double) failureRate);
	}

}
