/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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

	/** Constante associada � categoria Inicia��o Cient�fica (IC) */
	public static final int IC = 1;
	/** Constante associada � categoria Inicia��o Tecnol�gica (IT) */
	public static final int IT = 2;

	/** Mapa contendo as categorias de bolsas de pesquisa dispon�veis */
	private static Map<Integer, String> categorias = new TreeMap<Integer, String>();

	static {
		categorias.put(IC, "Inicia��o Cient�fica (IC)");
		categorias.put(IT, "Inicia��o Tecnol�gica (IT)");
	}

	/**
	 * Retorna a descri��o da categoria.
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
