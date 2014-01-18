/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.List;
import java.util.Stack;

import br.ufrn.sigaa.ensino.graduacao.dominio.FiltroListaDiscentesCalcular;

/**
 * Lista utilizada para armazenar ids de discentes cujos
 * cálculos serão realizados
 * 
 * @author David Pereira
 * 
 */
public class ListaDiscentesCalcular {

	// array de discentes
	private Stack<Integer> discentes = new Stack<Integer>();

	private int totalDiscentes = 0;

	private int totalProcessados = 0;

	public synchronized boolean possuiDiscentes() {
		return discentes.size() > 0;
	}

	// entrega o próximo discente para ser processado
	public synchronized Integer getProximoDiscente() {
		return discentes.pop();
	}

	public synchronized void registraProcessado() {
		totalProcessados++;
	}

	public void carregarDiscentes(FiltroListaDiscentesCalcular<Integer> metodo) {
		discentes.clear();
		
		List<Integer> discentesBuscados = metodo.execute();
		for ( Integer e : discentesBuscados )
			discentes.add(e);

		totalProcessados = 0;
		totalDiscentes = discentes.size();
	}

	public Stack<Integer> getDiscentes() {
		return discentes;
	}

	public int getTotalDiscentes() {
		return totalDiscentes;
	}

	public int getTotalProcessados() {
		return totalProcessados;
	}
	
}