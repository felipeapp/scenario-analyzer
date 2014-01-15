/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/05/2008
 *
 */
package br.ufrn.sigaa.vestibular.dominio;

import java.util.HashMap;
import java.util.Map;

/** Define o conceito de um fiscal durante o trabalho no vestibular.
 * 
 * @author Édipo Elder F. Melo
 *
 */
public class ConceitoFiscal {

	/** Define o conceito INSUFICIENTE. */
	public static final char INSUFICIENTE = 'I';
	
	/** Define o conceito SUFICIENTE. */
	public static final char SUFICIENTE = 'S';
	
	/** Mapa de descrições de conceitos. */
	public static Map<Character, String> descricaoConceitos = new HashMap<Character, String>() {{
		put(INSUFICIENTE, "INSUFICIENTE");
		put(SUFICIENTE, "SUFICIENTE");
	}}; 
}
