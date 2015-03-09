package br.ufrn.ppgsc.scenario.analyzer.cdynamic.parsers;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeGenericAnnotation;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimePerformance;
import br.ufrn.ppgsc.scenario.analyzer.common.annotations.Performance;

public class PerformanceAnnotationParser extends GenericAnnotationParser {

	public RuntimeGenericAnnotation getRuntimeAnnotation(Member member) {
		RuntimePerformance rp = null;
		Annotation annotation = ((AnnotatedElement) member).getAnnotation(Performance.class);

		if (annotation != null) {
			Performance pann = (Performance) annotation;
			rp = new RuntimePerformance(pann.name(), pann.limitTime());
		}

		return rp;
	}

}
