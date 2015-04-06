package br.ufrn.ppgsc.scenario.analyzer.cdynamic.aspects;

import java.lang.annotation.Annotation;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AspectScenarioEntryPoint extends AbstractAspectEntryPoint {

	public Class<? extends Annotation> getAnnotationClass() {
		return br.ufrn.ppgsc.scenario.analyzer.common.annotations.arq.Scenario.class;
	}

	@Pointcut("execution(@br.ufrn.ppgsc.scenario.analyzer.common.annotations.arq.Scenario * *(..))")
	public void entryPoint() {
	}

}
