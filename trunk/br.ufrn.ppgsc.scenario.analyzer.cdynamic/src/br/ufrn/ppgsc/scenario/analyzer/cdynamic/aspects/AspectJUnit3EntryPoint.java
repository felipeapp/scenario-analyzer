package br.ufrn.ppgsc.scenario.analyzer.cdynamic.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AspectJUnit3EntryPoint extends AbstractAspectEntryPoint {

	@Pointcut("within(junit.framework.TestCase+) && execution(* test*(..))")
	public void entryPoint() {
	}

}
