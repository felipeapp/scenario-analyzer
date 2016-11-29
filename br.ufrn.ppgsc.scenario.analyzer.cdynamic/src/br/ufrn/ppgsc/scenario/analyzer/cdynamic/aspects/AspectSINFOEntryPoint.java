package br.ufrn.ppgsc.scenario.analyzer.cdynamic.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AspectSINFOEntryPoint extends AbstractAspectEntryPoint {

	@Pointcut("within(@org.springframework.stereotype.Component *) && "
			+ "!execution(* set*(..)) && !execution(* get*(..)) && !execution(* is*(..)) && "
			+ "!within(*.UsuarioMBean) && !within(*.LoginActions) && !within(*.LogonAction)")
	public void entryPoint() {
	}

}