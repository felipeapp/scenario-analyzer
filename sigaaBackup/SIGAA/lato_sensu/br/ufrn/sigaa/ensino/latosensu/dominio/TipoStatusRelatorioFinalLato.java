/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '27/06/2008'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

import java.util.Map;
import java.util.TreeMap;

/**
 * Classe que armazena as constantes de status dos relat�rios finais de cursos lato sensu
 * @author leonardo
 *
 */
public class TipoStatusRelatorioFinalLato {

	/** Relat�rio Final preenchido parcialmente pelo usu�rio, por�m ainda n�o submetido. */
	public static final int INCOMPLETO = 1;
	
	/** Relat�rio Final submetido para avalia��o. */
	public static final int SUBMETIDO = 2;
	
	/** Relat�rio Final aprovado. */
	public static final int APROVADO = 3;

	/** Relat�rio Final N�o Aprovado */
	public static final int NAO_APROVADO = 4;

	/** Tipos dos status do Relat�rio Final. */
	private static Map<Integer, String> tipos = new TreeMap<Integer, String>();
	
	static {
		tipos.put(INCOMPLETO, "INCOMPLETO");
		tipos.put(SUBMETIDO, "SUBMETIDO");
		tipos.put(APROVADO, "APROVADO");
		tipos.put(NAO_APROVADO, "N�O APROVADO");
	}
	
	public static String getDescricao(int tipo) {
		return tipos.get(tipo);
	}
	
	public static Map<Integer, String> getTipos() {
		return tipos;
	}
}
