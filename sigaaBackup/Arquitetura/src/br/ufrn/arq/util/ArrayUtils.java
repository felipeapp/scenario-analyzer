/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.arq.util;

import java.lang.reflect.Array;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import br.ufrn.arq.erros.RuntimeNegocioException;

/**
 * Classe utilitária para auxiliar o trabalho com
 * arrays.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 */
public class ArrayUtils {

	/** Converte um array de Strings em um array de Integer. */
	public static Integer[] toIntArray(String[] params) {
		Integer[] intParams = new Integer[params.length];
		int i = 0;

		for (String p : params) {
			intParams[i++] = Integer.parseInt(p);
		}
		return intParams;
	}

	/** Converte um array de int's em um array de Integers. */
	public static Integer[] toIntegerArray(int array[]) {

		if (array != null) {
			Integer[] objWrapper = new Integer[array.length];
			for (int a = 0; a < array.length; a++) {
				objWrapper[a] = new Integer(array[a]);
			}
			return objWrapper;
		} else {
			return null;
		}

	}

	/** Converte um array de Strings em um array de Integer. */
	public static Long[] toLongArray(String[] params) {
		Long[] longParams = new Long[params.length];
		int i = 0;

		for (String p : params) {
			longParams[i++] = Long.parseLong(p);
		}
		return longParams;
	}
	
	/** Verifica se um nível está contido num array de níveis */
	public static boolean contains(char[] niveis, char nivel) {
		for (char nivei : niveis) {
			if (nivei == nivel)
				return true;
		}
		return false;
	}

	/**
	 * Verifica se um ID está em uma lista de IDs.
	 *
	 * @author yoshi
	 */
	public static boolean idContains(int id, int[] ids) {
		for (int id2 : ids) {
			if (id2 == id)
				return true;
		}
		return false;
	}
	
	/**
	 * Verifica se um ID está em uma lista de IDs.
	 *
	 * @author yoshi
	 */
	public static boolean idContains(Integer id, Integer[] ids) {
		for (int id2 : ids) {
			if (id2 == id)
				return true;
		}
		return false;
	}

	/**
	 * Agrupa os elementos dos arrays em um map para permitir uma manipulação posterior
	 * mais fácil.
	 * 
	 * @param classeChave a classe da chave do map
	 * @param classeValor a classe do valor do map. Se for um array, todos os elementos
	 * restantes do array são utilizados, em vez de só o segundo elemento.
	 * 
	 * @author Bráulio
	 */
	@SuppressWarnings("unchecked")
	public static <S,T> Map<S, T> agrupar( Collection<Object[]> arrays, Class<S> classeChave, Class<T> classeValor ) {
		Map<S,T> r = new TreeMap<S, T>();
		
		for ( Object[] a : arrays ) {
			S chave = converter(a[0], classeChave);
			T valor = null;
			
			if ( !!! classeValor.isArray() )
				valor = converter(a[1], classeValor);
			else {
				@SuppressWarnings("rawtypes")
				Class c = classeValor.getComponentType();
				
				Object[] x = (Object[]) Array.newInstance(c, a.length - 1);
				for ( int i = 1; i < a.length; i++ )
					x[i-1] =  converter(a[i], c);
				
				valor = (T) x;
			}
			
			r.put(chave, valor);
		}
		
		return r;
	}
	
	/**
	 * Agrupa os elementos dos arrays em um map para uma manipulação posterior mais fácil.
	 * Os três parâmetros cS, cT e cU significam:
	 * <ul>
	 *   <li> cS: classe da primeira chave.
	 *   <li> cT: classe da segunda chave.
	 *   <li> cU: classe do valor guardado. Se for um array, todos os elementos restantes do array
	 *   são utilizados, em vez de só o terceiro elemento.
	 * </ul>
	 * 
	 * @author Bráulio
	 */
	@SuppressWarnings("unchecked")
	public static <S,T,U> Map<S, Map<T, U>> agrupar( Collection<Object[]> arrays,
			Class<S> cS, Class<T> cT, Class<U> cU ) {
		Map<S,Map<T,U>> m = new TreeMap<S,Map<T,U>>();
		
		for ( Object[] a : arrays ) {
			S s = converter(a[0], cS);
			T t = converter(a[1], cT);
			U u = null;
			
			if ( !!! cU.isArray() )
				u = converter(a[2], cU);
			else {
				@SuppressWarnings("rawtypes")
				Class c = cU.getComponentType();
				
				Object[] x = (Object[]) Array.newInstance(c, a.length - 2);
				for ( int i = 2; i < a.length; i++ )
					x[i-2] =  converter(a[i], c);
				
				u = (U) x;
			}
			
			if ( !!! m.containsKey(s) )
				m.put(s, new TreeMap<T,U>());
			m.get(s).put(t, u);
		}
		
		return m;
	}
	
	/**
	 * Agrupa os elementos dos arrays em um map para uma manipulação posterior mais fácil.
	 * Os quatro parâmetros cS, cT, cU e cV significam:
	 * <ul>
	 *   <li> cS: classe da primeira chave.
	 *   <li> cT: classe da segunda chave.
	 *   <li> cU: classe da terceira chave.
	 *   <li> cV: classe do valor guardado. Se for um array, todos os elementos restantes 
	 *   do array são utilizados, em vez de só o quarto elemento.
	 * </ul>
	 * 
	 * @author Bráulio
	 */
	@SuppressWarnings("unchecked")
	public static <S,T,U,V> Map<S, Map<T, Map<U,V>>> agrupar( Collection<Object[]> arrays,
			Class<S> cS, Class<T> cT, Class<U> cU, Class<V> cV) {
		
		Map<S, Map<T, Map<U,V>>> m = new TreeMap<S, Map<T,Map<U,V>>>();
		
		for ( Object[] a : arrays ) {
			S s = converter(a[0], cS);
			T t = converter(a[1], cT);
			U u = converter(a[2], cU);
			V v = null;
			
			if ( !!! cV.isArray() )
				v = converter(a[3], cV);
			else {
				@SuppressWarnings("rawtypes")
				Class c = cV.getComponentType();
				
				Object[] x = (Object[]) Array.newInstance(c, a.length - 3);
				for ( int i = 3; i < a.length; i++ )
					x[i-3] =  converter(a[i], c);
				
				v = (V) x;
			}
			
			if ( !!! m.containsKey(s) )
				m.put(s, new TreeMap<T,Map<U,V>>());			
			if ( !!! m.get(s).containsKey(t) )
				m.get(s).put(t, new TreeMap<U,V>());
			m.get(s).get(t).put(u, v);
		}
		
		return m;
	}
	
	/**
	 * Tenta converter um valor ou um array de valores para o tipo básico passado.
	 */
	@SuppressWarnings("unchecked")
	private static <T> T converter( Object objeto, Class<T> classe ) {
		if ( objeto == null)
			return null;
		else if ( classe.equals(Object.class) )
			return (T) objeto;
		else if ( classe.equals(Integer.class) )
			if ( objeto instanceof Number )
				return (T) (Integer) ((Number)objeto).intValue();
			else
				return (T) Integer.valueOf(objeto.toString());
		else if ( classe.equals(String.class) )
			return (T) objeto.toString();
		else if ( classe.equals(Boolean.class) )
			return (T) Boolean.valueOf( objeto.toString() );
		else if ( classe.equals(Double.class) )
			if ( objeto instanceof Number )
				return (T) (Double) ((Number)objeto).doubleValue();
			else
				return (T) Double.valueOf( objeto.toString() );
		else if ( classe.isArray() ) {
			List<T> lista = new ArrayList<T>();
			Object[] array = (Object[]) objeto;
			for ( int i = 0; i < array.length; i++ )
				lista.add( (T) converter( array[0], classe.getComponentType() ) );
			return (T) lista.toArray();
		} else
			throw new RuntimeNegocioException(
					new InvalidParameterException(
							"Classe " + classe.getCanonicalName() + " não suportada.") );
	}

	public static boolean isEmptyValues(Object[] values) {
		if (values == null)
			return true;
		
		if (values.length == 0)
			return true;
		
		for (Object value : values) {
			if (ValidatorUtil.isEmpty(value))
				return true;
			else
				return false;
		}
		
		return false;
	}
}
