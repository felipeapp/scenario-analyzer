package br.ufrn.ppgsc.scenario.analyzer.cdynamic.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AspectSINFOEntryPoint extends AbstractAspectEntryPoint {

	/*@Pointcut("within(@org.springframework.stereotype.Component *) && "
			+ "!execution(* get*(..)) && !execution(* set*(..)) && !execution(* is*(..))")*/
	@Pointcut("within(@org.springframework.stereotype.Component *)")
	public void entryPoint() {
	}

	@Pointcut("cflow(execution(* set*(..)) || execution(* get*(..)) || execution(* is*(..)))")
	//@Pointcut("cflow(within(*..UsuarioMBean) || within(*..LoginActions) || within(*..LogonAction))")
	public void exclusionPoint() {
	}

}
