package br.ufrn.ppgsc.scenario.analyzer.cdynamic.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AspectMBeanEntryPoint extends AbstractAspectEntryPoint {

	// @Pointcut("execution(* (@org.springframework.stereotype.Component *).*(..)) || execution((@org.springframework.stereotype.Component *).new(..))")
	@Pointcut("within(@org.springframework.stereotype.Component *) && "
			+ "(execution(* *(..)) || execution(*.new(..))) && "
			+ "!execution(* set*(..)) && !execution(* get*(..)) && !execution(* is*(..))")
	public void entryPoint() {
	}

}
