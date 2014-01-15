/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Cria��o: 19/05/2009
 */
package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os par�metros de produ��o
 * intelectual.
 * @author David Pereira
 *
 */
public interface ParametrosProdocente {

	/** Relat�rio padr�o de pesquisa procedente */
	public static final String RELATORIO_PADRAO_PESQUISA = "2_11100_1";
	
	/** Relat�rio padr�o de progress�o procedente */
	public static final String RELATORIO_PADRAO_PROGRESSAO = "2_11100_2";
	
	/** Tamanho m�ximo (MB) do arquivo para envio */
	public static final String TAMANHO_MAXIMO_ENVIO_ARQUIVO_PRODUCAO = "2_11100_3";
	
	/** Url do servi�o de exporta��o de Curr�culos Lattes do CNPq */
	public static final String URL_WS_CURRICULO_LATTES = "2_11100_4";
	
	/** Usu�rio do servi�o de exporta��o de Curr�culos Lattes do CNPq */
	public static final String USER_WS_CURRICULO_LATTES = "2_11100_5";
	
	/** Senha do servi�o de exporta��o de Curr�culos Lattes do CNPq */
	public static final String PASSWD_WS_CURRICULO_LATTES = "2_11100_6";

	/** Define a quantidade de pessoas cujos curr�culos ser�o verificados pela rotina de importa��o autom�tica do Lattes. */
	public static final String TAMANHO_LOTE_IMPORTACAO_CV_LATTES = "2_11100_7";
	
	/** Define o intervalo m�nimo (em milissegundos) ap�s o qual uma pessoa pode ser verificada novamente pela rotina de importa��o autom�tica do Lattes. */
	public static final String INTERVALO_IMPORTACAO_CV_LATTES = "2_11100_8";
}
