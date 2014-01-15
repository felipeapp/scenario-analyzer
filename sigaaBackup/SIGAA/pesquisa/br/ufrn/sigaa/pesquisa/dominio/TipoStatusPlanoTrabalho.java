/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/01/2007
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;

import java.util.HashMap;
import java.util.Map;

/**
 * Constantes para os status dos planos de trabalho
 *
 * @author ricardo
 *
 */
public class TipoStatusPlanoTrabalho {

	/** Ap�s o cadastro do plano de trabalho */
	public static final int CADASTRADO = 1;
	/** Ap�s a distribui��o para avalia��o dos consultores */
	@Deprecated
	public static final int EM_AVALIACAO = 2;

	/** Aprovado pelo consultor */
	public static final int APROVADO = 3;
	/** N�o aprovado pelo consultor */
	public static final int NAO_APROVADO = 4;
	/** Ap�s a distribui��o de cotas de bolsas */
	public static final int AGUARDANDO_INDICACAO = 5;

	/** Plano de trabalho em andamento */
	public static final int EM_ANDAMENTO = 7;
	/** Plano de trabalho finalizado */
	public static final int FINALIZADO = 8;
	/** Aguardando re-submiss�o por parte do orientador */
	public static final int AGUARDANDO_RESUBMISSAO = 13;
	/** O projeto do plano n�o foi aprovado */
	public static final int PROJETO_NAO_APROVADO = 14;
	
	/** Aprovado pelo consultor mas com algumas restri��es */
	public static final int APROVADO_COM_RESTRICOES = 15;
	/** Corrigido pelo coordenador ap�s a aprova��o com restri��es */
	public static final int CORRIGIDO = 16;

	/** Plano de trabalho exclu�do */
	public static final int EXCLUIDO = 17;

	/** Respons�vel por apresentar todos os status possiveis do plano de trabalho */
	private static Map<Integer, String> tipos = new HashMap<Integer, String>();

	static {
		tipos.put(CADASTRADO, "CONCORRENDO A COTA");
		tipos.put(EM_AVALIACAO, "EM AVALIA��O");
		tipos.put(APROVADO, "APROVADO");
		tipos.put(NAO_APROVADO, "N�O APROVADO");
		tipos.put(AGUARDANDO_RESUBMISSAO, "AGUARDANDO RESUBMISS�O");
		tipos.put(AGUARDANDO_INDICACAO, "AGUARDANDO INDICA��O DE BOLSISTA");
		tipos.put(EM_ANDAMENTO, "EM ANDAMENTO");
		tipos.put(FINALIZADO, "FINALIZADO");
		tipos.put(AGUARDANDO_RESUBMISSAO, "AGUARDANDO RESUBMISS�O");
		tipos.put(PROJETO_NAO_APROVADO, "PROJETO N�O APROVADO");
		tipos.put(APROVADO_COM_RESTRICOES, "APROVADO COM RESTRI��ES");
		tipos.put(CORRIGIDO, "CORRIGIDO PELO ORIENTADOR");
		tipos.put(EXCLUIDO, "EXCLU�DO");
	}


	/** Retorna a descri��o do tipo de Status informado */
	public static String getDescricao(int status) {
		return tipos.get(status);
	}

	public static Map<Integer, String> getTipos() {
		return tipos;
	}

}
