/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 29/07/2009
 */
package br.ufrn.arq.aop;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspecto para realizar profiling de classes e métodos
 * com a anotação {@link Metrics}.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Component @Aspect
public class ProfilingAspect {

	private boolean profilingAtivo;
	
	public static final Map<String, String> COUNT_CALL_MAP = new ConcurrentHashMap<String, String>();
	
	public static final Map<String, String> MAX_TIME_MAP = new ConcurrentHashMap<String, String>();
	
	public static final Map<String, String> TIME_MAP = new ConcurrentHashMap<String, String>();
	
	/**
	 * Realiza profiling de métodos com a anotação {@link Metrics}.
	 * @param pjp
	 * @param metrics
	 * @return
	 * @throws Throwable
	 */
	@Around("@annotation(metrics)")
	public Object profileMethods(ProceedingJoinPoint pjp, Metrics metrics) throws Throwable {
		return profile(pjp, metrics);
	}
	
	/**
	 * Realiza profiling de métodos de classes que possuem a anotação {@link Metrics}.
	 * @param pjp
	 * @param metrics
	 * @return
	 * @throws Throwable
	 */
	@Around("@within(metrics)")
	public Object profileClasses(ProceedingJoinPoint pjp, Metrics metrics) throws Throwable {
		return profile(pjp, metrics);
	}
	
	/**
	 * Calcula as informações de profiling para o método que está sendo interceptado.
	 * @param pjp
	 * @param metrics
	 * @return
	 * @throws Throwable
	 */
	private Object profile(ProceedingJoinPoint pjp, Metrics metrics) throws Throwable {
		if (profilingAtivo) {
			MetricType[] value = metrics.value();
			String method = methodName(pjp);
				
			long init = System.currentTimeMillis();
			Object retVal = pjp.proceed();
			long finish = System.currentTimeMillis();
			
			if (ArrayUtils.contains(value, MetricType.COUNT_CALL)) {
				String countStr = COUNT_CALL_MAP.get(method);
				if (countStr == null) countStr = "0";
					
				int count = Integer.parseInt(countStr) + 1;
				COUNT_CALL_MAP.put(method, String.valueOf(count));
			}
			
			if (ArrayUtils.contains(value, MetricType.MAX_TIME)) {
				String timeStr = MAX_TIME_MAP.get(method);
				if (timeStr == null) timeStr = "0";
				
				long time = Long.parseLong(timeStr);
				if (finish - init > time)
					MAX_TIME_MAP.put(method, String.valueOf(finish - init));
			}
	
			if (ArrayUtils.contains(value, MetricType.TIME)) {
				String timeStr = TIME_MAP.get(method);
				if (timeStr == null) timeStr = "0|0";
		
				String[] timeArray = timeStr.split("\\|");
				
				long time = Long.parseLong(timeArray[0]);
				int count = Integer.parseInt(timeArray[1]);
				
				long media = (time * count + (finish - init)) / (count + 1);
				
				TIME_MAP.put(method, media + "|" + (count + 1));
			}
			
			return retVal;
		} else {
			return pjp.proceed();
		}
	}

	/**
	 * Retorna o nome do método que está sendo interceptado.
	 * @param pjp
	 * @return
	 */
	private String methodName(ProceedingJoinPoint pjp) {
		StringBuilder method = new StringBuilder(pjp.getTarget().getClass().getName() + "#" + pjp.getSignature().getName() + "(");
		for (int i = 0; i < pjp.getArgs().length; i++) {
			if (pjp.getArgs()[i] != null)
				method.append(pjp.getArgs()[i].getClass().getName());
			else
				method.append("null");
				
			if (i < pjp.getArgs().length - 1)
				method.append(", ");
		}
		method.append(")");
		return method.toString();
	}
	
	public boolean isProfilingAtivo() {
		return profilingAtivo;
	}
	
	public void setProfilingAtivo(boolean profilingAtivo) {
		this.profilingAtivo = profilingAtivo;
	}
	
}
