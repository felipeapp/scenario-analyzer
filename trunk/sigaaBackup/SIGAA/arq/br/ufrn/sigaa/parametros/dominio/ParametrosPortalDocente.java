/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/04/2010
 *
 */
package br.ufrn.sigaa.parametros.dominio;


/**
 * Interface contendo par�metros referentes ao Portal do Docente. 
 * 
 * @author agostinho campos
 *
 */
public interface ParametrosPortalDocente {

	/** Status que define o PID como CADASTRADO. */
	public static final String CADASTRADO = "2_12000_1";
	
	/** Status que define o PID como HOMOLOGADO. */
	public static final String HOMOLOGADO = "2_12000_2";
	
	/** Status que define o PID como ENVIADO PARA HOMOLOGACAO. */
	public static final String ENVIADO_HOMOLOGACAO = "2_12000_3";
	
	/** URL para a p�gina p�blica do docente. */
	public static final String URL_DOCENTE = "2_12000_4";
	
	/** Ano inicial para o cadastro do Plano Individual de Doc�ncia - PID */
	public static final String ANO_INICIAL_CADASTRO_PID = "2_12000_5";
	
	/** Per�odo inicial para o cadastro do Plano Individual de Doc�ncia - PID */
	public static final String PERIODO_INICIAL_CADASTRO_PID = "2_12000_6";
	
	/** Indica se o usu�rio poder� alterar um PID Homologado ou n�o. */
	public static final String PERMITE_ALTERAR_PID_HOMOLOGADO = "2_12000_7";
	
	/** Indica se o usu�rio poder� alterar um PID Homologado ou n�o. */
	public static final String NUMERO_FORUNS_PAGINACAO_FORUNS_CURSO = "2_12000_8";
}
