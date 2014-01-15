/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/04/2008
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import java.util.Map;
import java.util.TreeMap;

/**
 * Classe que armazena as constantes das categorias
 * de bolsas de pesquisa
 *
 * @author leonardo
 *
 */
public class CategoriaBolsaPesquisa {

	/** Constante associada à categoria Iniciação Científica (IC) */
	public static final int IC = 1;
	/** Constante associada à categoria Iniciação Tecnológica (IT) */
	public static final int IT = 2;

	/** Mapa contendo as categorias de bolsas de pesquisa disponíveis */
	private static Map<Integer, String> categorias = new TreeMap<Integer, String>();

	static {
		categorias.put(IC, "Iniciação Científica (IC)");
		categorias.put(IT, "Iniciação Tecnológica (IT)");
	}

	/**
	 * Retorna a descrição da categoria.
	 * @param categoria
	 * @return
	 */
	public static String getDescricao(int categoria) {
		return categorias.get(categoria);
	}

	public static Map<Integer, String> getCategorias() {
		return categorias;
	}
}
