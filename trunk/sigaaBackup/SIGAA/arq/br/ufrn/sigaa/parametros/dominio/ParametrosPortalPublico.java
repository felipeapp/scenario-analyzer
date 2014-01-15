/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on '21/09/2010'
 *
 */

package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os par�metros do Portal P�blico
 * 
 * @author Henrique Andr�
 * 
 */
public interface ParametrosPortalPublico {
	
	
	/** Seta o location(LC) padr�o para os portais p�blicos do SIGAA */
	public static final String LC_DEFAULT = "2_110600_0";

	/** Endere�o Eletr�nico de acesso aos portais p�blicos dos programas */
	public static final String URL_PROGRAMA = "2_110600_1";
	public static final String URL_DEPTO = "2_110600_2";
	public static final String URL_CURSO = "2_110600_3";
	public static final String URL_CENTRO = "2_110600_4";
	
	/** Endere�o Eletr�nico de acesso a institui��o */
	public static final String URL_INSTITUICAO = "2_110600_5";
	
	/** Limita quantidade de not�cias que deve ser exibida em destaque nos portais p�blcos do SIGAA */
	public static final String QTD_MAX_NOTICIAS = "2_110600_6";
	
	/** Limita quantidade de processos seletivos vis�veis na p�gina principal do portal p�blico dos programas e cursos no SIGAA */
	public static final String QTD_MAX_PROCESSOS_SELETIVOS = "2_110600_7";
	
	/** Define a quantidade de dias passados da data final de inscri��o no processo seletivo no portal p�blico dos programas no SIGAA */
	public static final String MAX_DIAS_PASSADOS_PROCESSO_SELETIVO = "2_110600_8";

	/** Define as situa��es que o docente pode ter acesso ao p�gina p�blica do docente */
	public static final String SITUACAO_SERVIDOR_ACESSO_PORTAL_DOCENTE = "2_110600_9";
	
	/** Define a logo da institui��o utlizada especificamente  no portal p�blico dos programas */
	public static final String LOGO_INSTITUICAO_PORTAL_PROGRAMA = "2_110600_10";
	
}
