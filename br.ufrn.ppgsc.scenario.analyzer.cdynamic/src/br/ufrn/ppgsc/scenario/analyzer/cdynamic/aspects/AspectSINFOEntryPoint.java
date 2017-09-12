package br.ufrn.ppgsc.scenario.analyzer.cdynamic.aspects;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.TypedValue;
import org.hibernate.hql.QueryTranslator;
import org.hibernate.hql.QueryTranslatorFactory;
import org.hibernate.hql.ast.ASTQueryTranslatorFactory;
import org.hibernate.impl.AbstractQueryImpl;
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
	//@Pointcut("within(org.apache.struts.actions.DispatchAction+) || within(@org.springframework.stereotype.Component *)")
	@Pointcut("within(@org.springframework.stereotype.Component *)")
	public void entryPoint() {
	}

	@Pointcut("cflow(execution(* set*(..)) || execution(* get*(..)) || execution(* is*(..)))")
	// @Pointcut("cflow(within(*..UsuarioMBean) || within(*..LoginActions) || within(*..LogonAction))")
	public void exclusionPoint() {
	}
	
	@SuppressAjWarnings
	@Before("exclusionPointFlow() && (call(* org.hibernate.Criteria.uniqueResult()) || call(* org.hibernate.Criteria.list()))")
	public void sqlFromCriteria(JoinPoint thisJoinPoint) {
		try {
			Criteria c = (Criteria) thisJoinPoint.getTarget();
			AspectUtil.addQueryToNode(criteriaToSql(c), "CRITERIA");
		}
		catch (Exception e) {
			printException(e, "CRITERIA");
		}
		
	}
	
	private void printException(Exception e, String type) {
		System.out.println("#$#$: " + type);
		System.out.println("#$#$: " + AspectUtil.getOrCreateRuntimeNodeStack().peek().getMemberSignature());
		//e.printStackTrace();
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

			if (parameters != null)
				for (Object p : parameters)
					sql = sql.replaceFirst("\\?", getSqlValue(p));
		}
		
		return sql;
	}
	
	private String getSqlValue(Object p) {
		String value;
		
		if (p instanceof TypedValue)
			p = ((TypedValue) p).getValue();
		
		if (p == null)
			throw new RuntimeException("Null parameter in getSqlValue");
		
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
		} else if (p instanceof Collection) {
			value = "";
			
			for (Object o : (Collection<?>) p)
				value += getSqlValue(o) + ",";
			
			value = value.substring(0, value.lastIndexOf(","));
		} else {
			value = p.toString();
		}
		
		return value;
	}
	
	@SuppressAjWarnings
	@Before("exclusionPointFlow() && call(* org.springframework.jdbc.core.JdbcTemplate.queryFor*(..))")
	public void sqlFromJDBCTemplate(JoinPoint thisJoinPoint) {
		try {
			AspectUtil.addQueryToNode(jdbcTemplateToSql(thisJoinPoint.getArgs()), "JDBC_TEMPLATE");
		} catch (Exception e) {
			printException(e, "JDBC_TEAMPLATE");
		}
	}
	
	private String jdbcTemplateToSql(Object[] args) {
		String sql = args[0].toString();
		Object[] values = null;

		if (args[1] instanceof Object[])
			values = (Object[]) args[1];
		else if (args[2] instanceof Object[])
			values = (Object[]) args[2];
		else
			System.err.println("Query method is not supported!");

		if (values != null)
			for (int i = 0; i < values.length; i++)
				sql = sql.replaceFirst("\\?", getSqlValue(values[i]));

		return sql;
	}
	
	@SuppressAjWarnings
	@Before("exclusionPointFlow() && (call(* org.hibernate.Query.uniqueResult()) || call(* org.hibernate.Query.list()))")
	public void sqlFromHibernateQuery(JoinPoint thisJoinPoint) {
		Object q = thisJoinPoint.getTarget();
		
		if (q instanceof SQLQuery) {
			try {
				AspectUtil.addQueryToNode(replaceHibernateQueryParameters((SQLQuery) q), "SQL_HIBERNATE");
			} catch (Exception e) {
				printException(e, "SQL_HIBERNATE");
			}
		}
		else if (q instanceof Query) {
			try {
				AspectUtil.addQueryToNode(hqlToSql((Query) q, thisJoinPoint.getThis()), "HQL");
			} catch (Exception e) {
				printException(e, "HQL");
			}
		}
	}

	private String replaceHibernateQueryParameters(Query q) {
		String sql = q.getQueryString();

		try {
			Field f = AbstractQueryImpl.class.getDeclaredField("namedParameters");
			f.setAccessible(true);
			Map<?, ?> p_map = (Map<?, ?>) f.get(q);

			for (Object p_name : p_map.keySet()) {
				String p_value = getSqlValue(p_map.get(p_name));
				sql = sql.replace(":" + p_name, p_value);
			}
			
			f = AbstractQueryImpl.class.getDeclaredField("namedParameterLists");
			f.setAccessible(true);
			p_map = (Map<?, ?>) f.get(q);

			for (Object p_name : p_map.keySet()) {
				String p_value = getSqlValue(p_map.get(p_name));
				sql = sql.replace(":" + p_name, p_value);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return sql;
	}
	
	private String hqlToSql(Query q, Object thisobj) {
		Session s = null;
		String sql = null;
		
		try {
			Method m = thisobj.getClass().getMethod("getSession");
			s = (Session) m.invoke(thisobj);
		} catch (Exception e) {
			System.err.println("The getSession() method does not exist!");
		}
		
		if (s != null) {
			String hql = replaceHibernateQueryParameters(q);
			
			QueryTranslatorFactory translatorFactory = new ASTQueryTranslatorFactory();
			SessionFactoryImplementor factory = (SessionFactoryImplementor) s.getSessionFactory();
			QueryTranslator translator = translatorFactory.createQueryTranslator(hql, hql, Collections.EMPTY_MAP, factory);
			
			translator.compile(Collections.EMPTY_MAP, false);
			sql = translator.getSQLString();
			sql = "select *" + sql.substring(sql.indexOf(" from "));
		}
		
		return sql;
	}

}
