package br.ufrn.ppgsc.scenario.analyzer.runtime.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;

import br.ufrn.ppgsc.scenario.analyzer.annotations.Security;
import br.ufrn.ppgsc.scenario.analyzer.runtime.model.RuntimeGenericAnnotation;
import br.ufrn.ppgsc.scenario.analyzer.runtime.model.RuntimeSecurity;

public class SecurityAnnotationParser extends GenericAnnotationParser {

	public RuntimeGenericAnnotation getRuntimeAnnotation(Member member) {
		RuntimeSecurity rs = null;
		Annotation annotation = ((AnnotatedElement) member).getAnnotation(Security.class);

		if (annotation != null) {
			Security sann = (Security) annotation;
			rs = new RuntimeSecurity(sann.name());
		}

		return rs;
	}

}
