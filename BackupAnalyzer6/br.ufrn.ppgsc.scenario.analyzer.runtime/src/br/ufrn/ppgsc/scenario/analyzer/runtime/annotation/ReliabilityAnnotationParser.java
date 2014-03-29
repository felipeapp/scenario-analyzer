package br.ufrn.ppgsc.scenario.analyzer.runtime.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;

import br.ufrn.ppgsc.scenario.analyzer.annotations.Reliability;
import br.ufrn.ppgsc.scenario.analyzer.runtime.model.RuntimeGenericAnnotation;
import br.ufrn.ppgsc.scenario.analyzer.runtime.model.RuntimeReliability;

public class ReliabilityAnnotationParser extends GenericAnnotationParser {

	public RuntimeGenericAnnotation getRuntimeAnnotation(Member member) {
		RuntimeReliability rr = null;
		Annotation annotation = ((AnnotatedElement) member).getAnnotation(Reliability.class);

		if (annotation != null) {
			Reliability rann = (Reliability) annotation;
			rr = new RuntimeReliability(rann.name(), rann.failureRate());
		}

		return rr;
	}

}
