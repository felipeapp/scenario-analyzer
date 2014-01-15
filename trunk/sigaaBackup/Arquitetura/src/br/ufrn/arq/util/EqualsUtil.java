/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 17/09/2006
 */
package br.ufrn.arq.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Possui métodos que facilitam a implementação do
 * <code>equals(Object obj)</code> nas classes do domínio
 *
 * Exemplo de uso:
 *
 * testEquals(obj1, obj2, "propriedade1", "propriedade2")
 *
 * @author Andre M Dantas
 *
 */
public class EqualsUtil {

	@SuppressWarnings("null")
	private static boolean testEquals(Object source, Object target, boolean testTransient, String... attributes) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (source == target) return true;

		if (source != null & target != null) {
			if (source.getClass().equals(target.getClass())) {
				if (attributes == null) {
					attributes = new String[0];
				}
				for (String attribute : attributes) {
					Object srcAtt = PropertyUtils
							.getProperty(source, attribute);
					Object tgtAtt = PropertyUtils
							.getProperty(target, attribute);
					if ("id".equals(attribute) && testTransient) {
						int srcId = (Integer) srcAtt;
						int tgtId = (Integer) tgtAtt;
						if ((srcId == 0 & tgtId != 0)
								|| (srcId != 0 & tgtId == 0)) break;
					}
					if (srcAtt != null) {
						if (srcAtt instanceof Date && tgtAtt instanceof Date) {
							if (((Date)srcAtt).getTime() != ((Date)tgtAtt).getTime()) 
								return false;
						} else if (!srcAtt.equals(tgtAtt)) {
							return false;
						}
					} else if (tgtAtt != null) return false;
				}
				return true;
			}
		}

		return false;
	}

	public static boolean testEquals(Object source, Object target, String... attributes) {
		try {
			return testEquals(source, target, false, attributes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * o teste de igualdade irá desconsiderar o atributo "ID" (se passado)
	 * @param source
	 * @param target
	 * @param attributes
	 * @return
	 */
	public static boolean testTransientEquals(Object source, Object target, String... attributes) {
		try {
			return testEquals(source, target, true, attributes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Testa todos os atributos
	 * @param source
	 * @param target
	 * @return
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static boolean testEquals(Object source, Object target) {
		try {
			String[] attributes = new String[source.getClass()
					.getDeclaredFields().length];
			for (int i = 0; i < attributes.length; i++) {
				attributes[i] = source.getClass().getDeclaredFields()[i]
						.getName();
			}
			return testEquals(source, target, attributes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
