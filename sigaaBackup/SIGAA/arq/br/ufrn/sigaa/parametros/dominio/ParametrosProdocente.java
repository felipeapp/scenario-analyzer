/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 19/05/2009
 */
package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os parâmetros de produção
 * intelectual.
 * @author David Pereira
 *
 */
public interface ParametrosProdocente {

	/** Relatório padrão de pesquisa procedente */
	public static final String RELATORIO_PADRAO_PESQUISA = "2_11100_1";
	
	/** Relatório padrão de progressão procedente */
	public static final String RELATORIO_PADRAO_PROGRESSAO = "2_11100_2";
	
	/** Tamanho máximo (MB) do arquivo para envio */
	public static final String TAMANHO_MAXIMO_ENVIO_ARQUIVO_PRODUCAO = "2_11100_3";
	
	/** Url do serviço de exportação de Currículos Lattes do CNPq */
	public static final String URL_WS_CURRICULO_LATTES = "2_11100_4";
	
	/** Usuário do serviço de exportação de Currículos Lattes do CNPq */
	public static final String USER_WS_CURRICULO_LATTES = "2_11100_5";
	
	/** Senha do serviço de exportação de Currículos Lattes do CNPq */
	public static final String PASSWD_WS_CURRICULO_LATTES = "2_11100_6";

	/** Define a quantidade de pessoas cujos currículos serão verificados pela rotina de importação automática do Lattes. */
	public static final String TAMANHO_LOTE_IMPORTACAO_CV_LATTES = "2_11100_7";
	
	/** Define o intervalo mínimo (em milissegundos) após o qual uma pessoa pode ser verificada novamente pela rotina de importação automática do Lattes. */
	public static final String INTERVALO_IMPORTACAO_CV_LATTES = "2_11100_8";
}
