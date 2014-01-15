/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 24/11/2009
 *
 */
package br.ufrn.sigaa.parametros.dominio;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/** Interface que define parâmetros utilizados na Avaliação Institucional. 
 * @author Édipo Elder F. Melo
 *
 */
public interface ParametrosAvaliacaoInstitucional {
	
	/** Prefixo utilizado na configuração dos parâmetros. Valor atual: "2_11500_" */
	public static final String PREFIX = Sistema.SIGAA + "_" + SigaaSubsistemas.PORTAL_AVALIACAO_INSTITUCIONAL.getId() + "_";
	
	/** Ano inicial da Avaliação Institucional. Ex.: 2001. */
	public static final String ANO_INICIO_AVALIACAO_INSTITUCIONAL = PREFIX + "1";
	
	/** Número Máximo de Threads para execução do processamento da Avaliação Institucional.*/
	public static final String NUM_MINIMO_AVALIACOES_PROCESSAMENTO_DOCENTE = PREFIX + "4";
	
	/** Parâmetro que indica se os docentes devem participar da avaliação institucional no período corrente. */
	public static final String AVALIACAO_DOCENTE_ATIVA = PREFIX + "5";//= "2_10100_6";
	
	/** Lista de ID das perguntas que serão apresentadas na consulta do resultado quantitativo da Avaliação Institucional pelos Discentes. */
	public static final String ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO = PREFIX + "6";

	/** Indica se no relatório analítico do resultado da avaliação irá ser exibido os comentários ao docente da turma e de trancamentos. */
	public static final String INCLUIR_COMENTARIOS_RELATORIO_ANALITICO = PREFIX +"7";
	
	/** ID do grupo de perguntas cuja a média será considerada com média geral do docente no Resultado da Avaliação Institucional. */
	public static final String ID_GRUPO_PERGUNTAS_MEDIA_GERAL_RESULTADO_AVALIACAO = PREFIX +"8";
	
	/** Resolução que regulamenta a Avaliação Institucional, em texto a ser exibido no formulário de Avaliação. Valor inicial: Resolução n&ordm; 131/2008 - CONSEPE. */
	public static final String RESOLUCAO_REGULAMENTADORA = PREFIX +"9";
	
	/** Indica se o discente é obrigado a preencher a Avaliação Institucional no período da matrícula. A Avaliação a ser preenchida é a do ano-período anterior. */
	public static final String AVALIACAO_DISCENTE_ATIVA = PREFIX + "10";
}
