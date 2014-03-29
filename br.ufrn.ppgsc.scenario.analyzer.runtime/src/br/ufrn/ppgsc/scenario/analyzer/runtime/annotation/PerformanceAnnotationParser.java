package br.ufrn.ppgsc.scenario.analyzer.runtime.annotation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.util.Properties;

import br.ufrn.ppgsc.scenario.analyzer.annotations.Performance;
import br.ufrn.ppgsc.scenario.analyzer.runtime.model.RuntimeGenericAnnotation;
import br.ufrn.ppgsc.scenario.analyzer.runtime.model.RuntimePerformance;

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
