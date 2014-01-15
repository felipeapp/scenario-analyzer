/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/04/2010
 */
package br.ufrn.arq.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Classe utilitária para auxiliar o trabalho com
 * Coleções.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 */
public class CollectionUtils {

	/**
	 * Converte uma coleção genérica em um ArrayList
	 */
	public static <T> ArrayList<T> toList(Collection<T> col) {
		ArrayList<T> list = new ArrayList<T>();
		list.addAll(col);
		return list;
	}

	/**
	 * Converte uma coleção genérica em um Set
	 */
	public static <T> Set<T> toSet(Collection<T> col) {
		HashSet<T> set = new HashSet<T>();
		set.addAll(col);
		return set;
	}
	
	/**
	 * Remove um elemento de uma posição de uma Collection
	 */
	public static <T> void removePorPosicao(Collection<T> col, int posicao) {
		List<T> lista;
		if (col != null) {

			if (posicao < 0 || posicao >= col.size()) {
				return;
			}

			if (col instanceof List<?>) {
				lista = (List<T>) col;
				lista.remove(posicao);
			}
			else {
				lista = toArrayList(col);
				lista.remove(posicao);
				col.clear();
				col.addAll(lista);
			}
		}
	}
	
	/**
	 * Transforma uma coleção qualquer em ArrayList
	 */
	public static <T> ArrayList<T> toArrayList(Collection<T> col) {
		ArrayList<T> list = new ArrayList<T>();
		if (col != null) {
			list.addAll(col);
		}
		return list;
	}
	
	/**
	 * Converte uma coleção em um TreeSet
	 */
	public static <T> TreeSet<T> toTreeSet(Collection<T> col) {
		TreeSet<T> treeSet = new TreeSet<T>();

		for (T t : col) {
			treeSet.add(t);
		}

		return treeSet;
	}

	/**
	 * Converte uma coleção em um HashSet
	 */
	public static <T> HashSet<T> toHashSet(Collection<T> col) {
		HashSet<T> hashSet = new HashSet<T>();

		for (T t : col) {
			hashSet.add(t);
		}

		return hashSet;
	}
	
	/** Soma os elementos de uma coleção de inteiros. */
	public static int somaInts( Collection<Integer> numeros ) {
		int soma = 0;
		for ( Integer i : numeros )
			soma += i;
		return soma;
	}
	
	/** Soma os elementos de uma coleção de doubles. */
	public static double somaDoubles( Collection<Double> numeros ) {
		int soma = 0;
		for ( Double i : numeros )
			soma += i;
		return soma;
	}
	
	/** Soma os elementos de uma coleção de longs. */
	public static double somaLongs( Collection<Long> numeros ) {
		int soma = 0;
		for ( Long i : numeros )
			soma += i;
		return soma;
	}
	
	/** Soma os elementos de uma coleção de floats. */
	public static double somaFloats( Collection<Float> numeros ) {
		int soma = 0;
		for ( Float i : numeros )
			soma += i;
		return soma;
	}
	
	/**
	 * Soma os elementos de um map duplamente aninhado, agrupando as somas pela primeiro
	 * chave.
	 * @author Bráulio
	 */
	public static <S, T> Map<S, Integer> somaIntsAgrupandoPorS( Map<S, Map<T, Integer> > map ) {
		Map<S, Integer> somas = new TreeMap<S, Integer>();
		
		for ( Map.Entry<S, Map<T, Integer>> entry : map.entrySet() )
			somas.put(entry.getKey(), somaInts(entry.getValue().values()) );
		
		return somas;
	}
	
	/**
	 * Soma os elementos de um map duplamente aninhado, agrupando as somas pela segunda
	 * chave.
	 * @author Bráulio
	 */
	public static <S, T> Map<T, Integer> somaIntsAgrupandoPorT( Map<S, Map<T, Integer> > map ) {
		Map<T, Integer> somas = new TreeMap<T, Integer>();
		
		for ( Map<T, Integer> mapAtual : map.values() )
			for ( Map.Entry<T, Integer> entry: mapAtual.entrySet() ) {
				if ( !!! somas.containsKey(entry.getKey()) )
					somas.put(entry.getKey(), 0);
				int atual = somas.get(entry.getKey());
				somas.put(entry.getKey(), atual + entry.getValue());
			}
		
		return somas;
	}
	
}
