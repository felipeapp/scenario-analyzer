/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/12/2008
 *
 */
package br.ufrn.sigaa.espacofisico.dominio;

import java.util.HashMap;
import java.util.Map;


/**
 * Situações possíveis, dentro do fluxo de solicitações e atendimentos, para uma 
 * reserva de utilização de um espaço físico
 * 
 * @author wendell
 *
 */
public class StatusReserva{
	
	public static final Integer SOLICITADA = 1;
	public static final Integer APROVADA = 2;
	public static final Integer NEGADA = 3;

	private static final Map<Integer,String> descricoesStatus;
	
	static {
		descricoesStatus = new HashMap<Integer, String>();
		descricoesStatus.put(SOLICITADA, "SOLICITADA");
		descricoesStatus.put(APROVADA, "APROVADA");
		descricoesStatus.put(NEGADA, "NEGADA");
	}
	
	public String getDescricao(Integer status) {
		return descricoesStatus.get(status);
	}
}
