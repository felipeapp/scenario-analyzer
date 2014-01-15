/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 24/11/2009
 *
 */
package br.ufrn.sigaa.parametros.dominio;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/** Interface que define par�metros utilizados na Avalia��o Institucional. 
 * @author �dipo Elder F. Melo
 *
 */
public interface ParametrosAvaliacaoInstitucional {
	
	/** Prefixo utilizado na configura��o dos par�metros. Valor atual: "2_11500_" */
	public static final String PREFIX = Sistema.SIGAA + "_" + SigaaSubsistemas.PORTAL_AVALIACAO_INSTITUCIONAL.getId() + "_";
	
	/** Ano inicial da Avalia��o Institucional. Ex.: 2001. */
	public static final String ANO_INICIO_AVALIACAO_INSTITUCIONAL = PREFIX + "1";
	
	/** N�mero M�ximo de Threads para execu��o do processamento da Avalia��o Institucional.*/
	public static final String NUM_MINIMO_AVALIACOES_PROCESSAMENTO_DOCENTE = PREFIX + "4";
	
	/** Par�metro que indica se os docentes devem participar da avalia��o institucional no per�odo corrente. */
	public static final String AVALIACAO_DOCENTE_ATIVA = PREFIX + "5";//= "2_10100_6";
	
	/** Lista de ID das perguntas que ser�o apresentadas na consulta do resultado quantitativo da Avalia��o Institucional pelos Discentes. */
	public static final String ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO = PREFIX + "6";

	/** Indica se no relat�rio anal�tico do resultado da avalia��o ir� ser exibido os coment�rios ao docente da turma e de trancamentos. */
	public static final String INCLUIR_COMENTARIOS_RELATORIO_ANALITICO = PREFIX +"7";
	
	/** ID do grupo de perguntas cuja a m�dia ser� considerada com m�dia geral do docente no Resultado da Avalia��o Institucional. */
	public static final String ID_GRUPO_PERGUNTAS_MEDIA_GERAL_RESULTADO_AVALIACAO = PREFIX +"8";
	
	/** Resolu��o que regulamenta a Avalia��o Institucional, em texto a ser exibido no formul�rio de Avalia��o. Valor inicial: Resolu��o n&ordm; 131/2008 - CONSEPE. */
	public static final String RESOLUCAO_REGULAMENTADORA = PREFIX +"9";
	
	/** Indica se o discente � obrigado a preencher a Avalia��o Institucional no per�odo da matr�cula. A Avalia��o a ser preenchida � a do ano-per�odo anterior. */
	public static final String AVALIACAO_DISCENTE_ATIVA = PREFIX + "10";
}
