package br.ufrn.ppgsc.scenario.analyzer.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/* TODO: Ver como juntar esses métodos com os outros de assinatura
 * sem gerar problemas com as dependências do classpath
 */
public abstract class MemberUtil {
	
	public static String getStandartMethodSignature(Member member) {
		StringBuffer result = new StringBuffer();
		
		result.append(member.getDeclaringClass().getName());
		result.append(".");
		result.append(member.getName());
		result.append("(");
		
		Class<?>[] ptypes = null;
		
		if (member instanceof Method)
			ptypes = ((Method) member).getParameterTypes();
		else if (member instanceof Constructor)
			ptypes = ((Constructor<?>) member).getParameterTypes();
		
		for (Class<?> cls : ptypes) {
			result.append(cls.getCanonicalName());
			result.append(",");
		}
		
		if (result.charAt(result.length() - 1) == ',')
			result.deleteCharAt(result.length() - 1);
		
		return result + ")";
	}
	
}
