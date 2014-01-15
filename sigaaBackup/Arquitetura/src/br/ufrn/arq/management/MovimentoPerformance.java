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
 * Classe que mapeia a performance dos movimentos
 *
 * @author Gleydson Lima
 *
 */
public class MovimentoPerformance {

	private Hashtable<Integer, Double> media;

	private static MovimentoPerformance singleton = new MovimentoPerformance();

	private MovimentoPerformance() {
		media = new Hashtable<Integer, Double>();
	}

	public static MovimentoPerformance getInstance() {
		return singleton;
	}

	public synchronized void registerMovimento(int codMovimento, long tempo) {

		Double tempoMedio = media.get(codMovimento);
		if (tempoMedio == null) {
			media.put(codMovimento, tempo / 1000d);
		} else {
			tempoMedio = (tempoMedio + tempo / 1000d) / 2;
			media.put(codMovimento, tempoMedio);
		}

	}

	public Collection<?> getMovimentos() {
        Object[] ordenado = media.keySet().toArray();
        Arrays.sort(ordenado);

        return Arrays.asList(ordenado);
	}

	public double getMedia(int codMov) {
		return media.get(codMov);
	}

}
