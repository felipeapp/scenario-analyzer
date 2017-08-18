package br.ufrn.ppgsc.scenario.analyzer.cdynamic.aspects;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.SuppressAjWarnings;

@Aspect
public class AspectSINFOEntryPoint extends AbstractAspectEntryPoint {

	/*
	 * @Pointcut("within(@org.springframework.stereotype.Component *) && " +
	 * "!execution(* get*(..)) && !execution(* set*(..)) && !execution(* is*(..))")
	 */
	// @Pointcut("within(org.apache.struts.actions.DispatchAction+)")
	// @Pointcut("within(@org.springframework.stereotype.Component *)")
	@Pointcut("within(org.apache.struts.actions.DispatchAction+) || within(@org.springframework.stereotype.Component *)")
	public void entryPoint() {
	}

	@Pointcut("cflow(execution(* set*(..)) || execution(* get*(..)) || execution(* is*(..)))")
	// @Pointcut("cflow(within(*..UsuarioMBean) || within(*..LoginActions) || within(*..LogonAction))")
	public void exclusionPoint() {
	}

	@SuppressAjWarnings
	@Before("exclusionPointFlow() && (call(* org.hibernate.Session.createSQLQuery(String)) || call(* org.hibernate.Session.createQuery(String)))")
	public void sqlFromHBCalls(JoinPoint thisJoinPoint) {
		String sql = thisJoinPoint.getArgs()[0].toString();
		AspectUtil.setParameter(sql);
	}

	@SuppressAjWarnings
	@Before("exclusionPointFlow() && call(* org.hibernate.Session.createCriteria(..))")
	public void criteriaFromHBCalls(JoinPoint thisJoinPoint) {
		String sql = Arrays.toString(thisJoinPoint.getArgs());
		AspectUtil.setParameter(sql);
	}

	@SuppressAjWarnings
	@Before("exclusionPointFlow() && call(* org.springframework.jdbc.core.JdbcTemplate.query(..))")
	public void sqlFromJdbcTemplateCalls(JoinPoint thisJoinPoint) {
		Object[] args = thisJoinPoint.getArgs();

		String sql = args[0].toString();
		String parameters = Arrays.toString((Object[]) args[1]);
		String mapper = args[2].getClass().getName();

		AspectUtil.setParameter(sql + ";" + parameters + ";" + mapper);
	}

}
