package br.ufrn.ppgsc.scenario.analyzer.cdynamic.parsers;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;

import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeGenericAnnotation;
import br.ufrn.ppgsc.scenario.analyzer.cdynamic.model.RuntimeSecurity;
import br.ufrn.ppgsc.scenario.analyzer.common.annotations.Security;

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
