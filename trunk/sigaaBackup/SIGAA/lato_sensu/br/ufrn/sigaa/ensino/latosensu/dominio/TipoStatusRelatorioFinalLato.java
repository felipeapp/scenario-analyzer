/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/06/2008'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

import java.util.Map;
import java.util.TreeMap;

/**
 * Classe que armazena as constantes de status dos relatórios finais de cursos lato sensu
 * @author leonardo
 *
 */
public class TipoStatusRelatorioFinalLato {

	/** Relatório Final preenchido parcialmente pelo usuário, porém ainda não submetido. */
	public static final int INCOMPLETO = 1;
	
	/** Relatório Final submetido para avaliação. */
	public static final int SUBMETIDO = 2;
	
	/** Relatório Final aprovado. */
	public static final int APROVADO = 3;

	/** Relatório Final Não Aprovado */
	public static final int NAO_APROVADO = 4;

	/** Tipos dos status do Relatório Final. */
	private static Map<Integer, String> tipos = new TreeMap<Integer, String>();
	
	static {
		tipos.put(INCOMPLETO, "INCOMPLETO");
		tipos.put(SUBMETIDO, "SUBMETIDO");
		tipos.put(APROVADO, "APROVADO");
		tipos.put(NAO_APROVADO, "NÃO APROVADO");
	}
	
	public static String getDescricao(int tipo) {
		return tipos.get(tipo);
	}
	
	public static Map<Integer, String> getTipos() {
		return tipos;
	}
}
