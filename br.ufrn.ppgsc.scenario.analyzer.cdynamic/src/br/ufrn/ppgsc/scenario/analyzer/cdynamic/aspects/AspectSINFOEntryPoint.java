package br.ufrn.ppgsc.scenario.analyzer.cdynamic.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AspectSINFOEntryPoint extends AbstractAspectEntryPoint {

	@Pointcut("within(@org.springframework.stereotype.Component *)")
	public void entryPoint() {
	}

	@Pointcut("cflow(within(*.UsuarioMBean) || within(*.LoginActions) || within(*.LogonAction)) && (execution(* *(..)) || execution(*.new(..)))")
	public void exclusionPoint() {
	}

}