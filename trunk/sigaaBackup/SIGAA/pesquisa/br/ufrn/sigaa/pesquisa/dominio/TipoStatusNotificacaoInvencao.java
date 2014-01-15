/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2009
 *
 */

package br.ufrn.sigaa.pesquisa.dominio;

import java.util.HashMap;
import java.util.Map;

/**
 * Constantes para os status das notificações de invenção
 * 
 * @author Leonardo Campos
 *
 */
public class TipoStatusNotificacaoInvencao {

	public static final int GRAVADA = 1;
	public static final int SUBMETIDA = 2;
	public static final int NECESSITA_CORRECOES = 3;
	public static final int EM_ANALISE_TECNICA = 4;
	public static final int ACEITA = 5;
	public static final int NEGADA = 6;
	public static final int REMOVIDA = 10;

	private static Map<Integer, String> tipos = new HashMap<Integer, String>();

	static {
		tipos.put(GRAVADA, "GRAVADA");
		tipos.put(SUBMETIDA, "SUBMETIDA");
		tipos.put(NECESSITA_CORRECOES, "NECESSITA CORREÇÕES");
		tipos.put(EM_ANALISE_TECNICA, "EM ANÁLISE TÉCNICA");
		tipos.put(ACEITA, "ACEITA");
		tipos.put(NEGADA, "NEGADA");
		tipos.put(REMOVIDA, "REMOVIDA");
	}
	
	public static String getDescricao(int status) {
		return tipos.get(status);
	}

	public static Map<Integer, String> getTipos() {
		return tipos;
	}
}
