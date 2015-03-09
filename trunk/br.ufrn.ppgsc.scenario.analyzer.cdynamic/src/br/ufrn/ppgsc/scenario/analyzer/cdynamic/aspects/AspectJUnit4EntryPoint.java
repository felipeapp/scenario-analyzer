package br.ufrn.ppgsc.scenario.analyzer.cdynamic.aspects;

import java.lang.annotation.Annotation;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AspectJUnit4EntryPoint extends AbstractAspectEntryPoint {

	public Class<? extends Annotation> getAnnotationClass() {
		return org.junit.Test.class;
	}

	@Pointcut("execution(@org.junit.Test * *(..))")
	public void entryPoint() {
	}

}
