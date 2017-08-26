package br.ufrn.ppgsc.scenario.analyzer.cdynamic.aspects;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.hql.QueryTranslator;
import org.hibernate.hql.QueryTranslatorFactory;
import org.hibernate.hql.ast.ASTQueryTranslatorFactory;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.impl.SessionImpl;
import org.hibernate.loader.OuterJoinLoader;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;

@Aspect
public class AspectSINFOEntryPoint extends AbstractAspectEntryPoint {

	/*
	 * @Pointcut("within(@org.springframework.stereotype.Component *) && " +
	 * "!execution(* get*(..)) && !execution(* set*(..)) && !execution(* is*(..))")
	 */
	@Pointcut("within(org.apache.struts.actions.DispatchAction+) || within(@org.springframework.stereotype.Component *)")
	public void entryPoint() {
	}

	@Pointcut("cflow(execution(* set*(..)) || execution(* get*(..)) || execution(* is*(..)))")
	// @Pointcut("cflow(within(*..UsuarioMBean) || within(*..LoginActions) || within(*..LogonAction))")
	public void exclusionPoint() {
	}
	
	@SuppressAjWarnings
	@Before("exclusionPointFlow() && (call(* org.hibernate.Criteria.uniqueResult()) || call(* org.hibernate.Criteria.list()))")
	public void sqlFromCriteria(JoinPoint thisJoinPoint) {
		Criteria c = (Criteria) thisJoinPoint.getTarget();
		AspectUtil.setParameter(criteriaToSql(c), "CRITERIA");
	}
	
	private String criteriaToSql(Criteria c) {
		String sql = null;
		Object[] parameters = null;
		
		try {
			CriteriaImpl cImpl = (CriteriaImpl) c;
			SessionImpl sImpl = (SessionImpl) cImpl.getSession();
			SessionFactoryImplementor factory = (SessionFactoryImplementor) sImpl.getSessionFactory();
			
			String[] implementors = factory.getImplementors(cImpl.getEntityOrClassName());
			CriteriaLoader loader = new CriteriaLoader((OuterJoinLoadable) factory.getEntityPersister(implementors[0]), factory, cImpl, implementors[0], sImpl.getEnabledFilters());
			
			Field sql_field = OuterJoinLoader.class.getDeclaredField("sql");
			sql_field.setAccessible(true);
			sql = (String) sql_field.get(loader);
			
			Field param_field = CriteriaLoader.class.getDeclaredField("translator");
			param_field.setAccessible(true);
			CriteriaQueryTranslator translator = (CriteriaQueryTranslator) param_field.get(loader);
			parameters = translator.getQueryParameters().getPositionalParameterValues();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		if (sql != null) {
			sql = "select *" + sql.substring(sql.indexOf(" from "));

			if (parameters != null) {
				for (Object p : parameters) {
					String value;
					
					if (p instanceof Boolean) {
						value = (Boolean) p ? "true" : "false";
					} else if (p instanceof String) {
						value = "'" + p + "'";
					} else if (p instanceof Class) {
						value = "'" + ((Class<?>) p).getCanonicalName() + "'";
					} else if (p instanceof Date) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
						value = "'" + sdf.format((Date) p) + "'";
					} else if (p instanceof Enum) {
						value = "" + ((Enum<?>) p).ordinal();
					} else {
						value = p.toString();
					}
					
					sql = sql.replaceFirst("\\?", value);
				}
			}
		}
		
		return sql;
	}
	
	@SuppressAjWarnings
	@Before("exclusionPointFlow() && call(* org.hibernate.Session.createSQLQuery(String))")
	public void sqlFromSQLQuery(JoinPoint thisJoinPoint) {
		String sql = thisJoinPoint.getArgs()[0].toString();
		AspectUtil.setParameter(sql, "SQL_HIBERNATE");
	}
	
	@SuppressAjWarnings
	@Before("exclusionPointFlow() && call(* org.springframework.jdbc.core.JdbcTemplate.queryFor*(..))")
	public void sqlFromJDBCTemplate(JoinPoint thisJoinPoint) {
		String sql = thisJoinPoint.getArgs()[0].toString();
		AspectUtil.setParameter(sql, "JDBC_TEMPLATE");
	}
	
	@SuppressAjWarnings
	@Before("exclusionPointFlow() && call(* org.hibernate.Session.createQuery(String))")
	public void sqlFromHQLQuery(JoinPoint thisJoinPoint) {
		String hql = thisJoinPoint.getArgs()[0].toString();
		AspectUtil.setParameter(hqlToSql(hql, thisJoinPoint.getThis()), "HQL");
	}

	private String hqlToSql(String hql, Object thisobj) {
		Session s = null;
		String sql = null;
		
		try {
			Method m = thisobj.getClass().getMethod("getSession");
			s = (Session) m.invoke(thisobj);
		} catch (Exception e) {
			System.err.println("The getSession() method does not exist!");
		}
		
		if (s != null) {
			QueryTranslatorFactory translatorFactory = new ASTQueryTranslatorFactory();
			SessionFactoryImplementor factory = (SessionFactoryImplementor) s.getSessionFactory();
			QueryTranslator translator = translatorFactory.createQueryTranslator(hql, hql, Collections.EMPTY_MAP, factory);
			
			translator.compile(Collections.EMPTY_MAP, false);
			sql = translator.getSQLString();
		}
		
		return sql;
	}

}
