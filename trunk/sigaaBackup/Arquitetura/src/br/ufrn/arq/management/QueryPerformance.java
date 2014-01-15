/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 05/07/2006
 */
package br.ufrn.arq.management;

import java.util.Arrays;
import java.util.Collection;
import java.util.Hashtable;

/**
 *
 * Classe que mapeia a performance das querys via Hibernate
 *
 * @author Gleydson Lima
 *
 */
@Deprecated
public class QueryPerformance {

	private Hashtable<String, Double> media;

	private static QueryPerformance singleton = new QueryPerformance();

	private QueryPerformance() {
		media = new Hashtable<String, Double>();
	}

	public static QueryPerformance getInstance() {
		return singleton;
	}

	public synchronized void registerQuery(String dao, Object obj,
			String nameOperacao, long tempo) {

		if (obj != null) {
			Class<?> dominio = null;
		
			if ( ! (obj instanceof Class<?>) ) {
				dominio = obj.getClass();
			} else {
				dominio = (Class<?>) obj;
			}
			String key = dao
					+ ","
					+ dominio.toString().substring(
							dominio.toString().lastIndexOf(".") + 1) + ","
					+ nameOperacao;

			Double tempoMedio = media.get(key);
			if (tempoMedio == null) {
				media.put(key, tempo / 1000d);
			} else {
				tempoMedio = (tempoMedio + tempo / 1000d) / 2;
				media.put(key, tempoMedio);
			}
		}

	}

	public synchronized void registerQuery(String key, long tempo) {

			Double tempoMedio = media.get(key);
			if (tempoMedio == null) {
				media.put(key, tempo / 1000d);
			} else {
				tempoMedio = (tempoMedio + tempo / 1000d) / 2;
				media.put(key, tempoMedio);
			}
		}

	public Collection<?> getQuerys() {

		Object[] ordenado = media.keySet().toArray();
		Arrays.sort(ordenado);

		return Arrays.asList(ordenado);

	}

	public double getMedia(String key) {
		return media.get(key);
	}

}
