/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '21/09/2010'
 *
 */

package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os parâmetros do Portal Público
 * 
 * @author Henrique André
 * 
 */
public interface ParametrosPortalPublico {
	
	
	/** Seta o location(LC) padrão para os portais públicos do SIGAA */
	public static final String LC_DEFAULT = "2_110600_0";

	/** Endereço Eletrônico de acesso aos portais públicos dos programas */
	public static final String URL_PROGRAMA = "2_110600_1";
	public static final String URL_DEPTO = "2_110600_2";
	public static final String URL_CURSO = "2_110600_3";
	public static final String URL_CENTRO = "2_110600_4";
	
	/** Endereço Eletrônico de acesso a instituição */
	public static final String URL_INSTITUICAO = "2_110600_5";
	
	/** Limita quantidade de notícias que deve ser exibida em destaque nos portais públcos do SIGAA */
	public static final String QTD_MAX_NOTICIAS = "2_110600_6";
	
	/** Limita quantidade de processos seletivos visíveis na página principal do portal público dos programas e cursos no SIGAA */
	public static final String QTD_MAX_PROCESSOS_SELETIVOS = "2_110600_7";
	
	/** Define a quantidade de dias passados da data final de inscrição no processo seletivo no portal público dos programas no SIGAA */
	public static final String MAX_DIAS_PASSADOS_PROCESSO_SELETIVO = "2_110600_8";

	/** Define as situações que o docente pode ter acesso ao página pública do docente */
	public static final String SITUACAO_SERVIDOR_ACESSO_PORTAL_DOCENTE = "2_110600_9";
	
	/** Define a logo da instituição utlizada especificamente  no portal público dos programas */
	public static final String LOGO_INSTITUICAO_PORTAL_PROGRAMA = "2_110600_10";
	
}
