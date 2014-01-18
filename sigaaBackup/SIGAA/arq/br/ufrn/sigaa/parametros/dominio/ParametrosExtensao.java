/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 19/05/2009
 */
package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os parâmetros do módulo
 * de extensão.
 * @author David Pereira
 *
 */
public interface ParametrosExtensao {

	/**
	 * Lista (com elementos separados por vírgula) dos IDs de tipos de bolsas de extensão
	 */
	public static final String LISTA_BOLSAS_EXTENSAO = "2_10900_1";
	
	/**
	 * Nome do Pró-Reitor de extensão. Utilizado nos certificados.
	 */
	public static final String NOME_PRO_REITOR_EXTENSAO = "2_10900_2";
	
	/**
	 * Número do telefone da pró-reitoria de extensão. Utilizado nos certificados.
	 */
	public static final String TELEFONE_PRO_REITORIA_EXTENSAO = "2_10900_18";
	
	/**
	 * Número do fax da pró-reitoria de extensão. Utilizado nos certificados.
	 */
	public static final String FAX_PRO_REITORIA_EXTENSAO = "2_10900_19";
	
	/**
	 * Endereço do e-mail da pró-reitoria de extensão. Utilizado nos certificados.
	 */
	public static final String EMAIL_PRO_REITORIA_EXTENSAO = "2_10900_20";
	
	/**
	 * Endereço do sitio da pró-reitoria de extensão. Utilizado nos certificados.
	 */
	public static final String SITIO_PRO_REITORIA_EXTENSAO = "2_10900_21";
	
	/**
	 * Total de ações ativas que um docente pode Coordenar simultaneamente.
	 */
	public static final String TOTAL_ACOES_ATIVAS_MESMA_MODALIDADE_COORDENADAS = "2_10900_3";
	
	/**
	 * Quantidade máxima de atraso do Relatório Final após o término de uma Ação de Extensão. 
	 */
	public static final String TOTAL_MAXIMO_DIAS_PERMITIDO_ATRASO_RELATORIO = "2_10900_4";
	
	/**
	 * Identificador da bolsa do fundo de apoio de extensão no SIPAC. 
	 */
	public static final String BOLSA_EXTENSAO = "2_10900_5";
	
	/**
	 * Identificador de quantos dias a data fim da ação pode ser ultrapassada.
	 */
	public static final String DIAS_ENCERRAR_ACOES_PENDENCIAS_EXECUCAO = "2_10900_7";
	
	/**
	 * Sigla do Fundo de Extensão Padrão.
	 */
	public static final String SIGLA_FUNDO_EXTENSAO_PADRAO = "2_10900_8";
	
	/**
	 * Nome do Fundo de Extensão Padrão.
	 */
	public static final String NOME_FUNDO_EXTENSAO_PADRAO = "2_10900_9";

	/**
	 * Responsável pela exibição do link para avaliação final de proposta.
	 */
	public static final String EXIBIR_AVALIACAO_FINAL_PROPOSTA = "2_10900_10";
	
	/**
	 * Responsável pela verificação da data limite de alteração da Carga horária da Equipe Executora
	 */
	public static final String DATA_LIMITE_ALTERACAO_CH_EQUIPE_EXECUTORA = "2_10900_11";
	
	/**
	 * Dia inicial de indicação de bolsista para os planos de trabalho, para entrar na folha de pagamento do mês corrente
	 */
	public static final String DIA_INICIAL_INDICACAO_BOLSISTA = "2_10900_12";

	/**
	 * Dia final de indicação de bolsista para os planos de trabalho, para entrar na folha de pagamento do mês corrente
	 */
	public static final String DIA_FINAL_INDICACAO_BOLSISTA = "2_10900_13";
	
	
	/**
	 * A frequência mínima necessária para a emissão de certificados para cursos de extensão.  Exemplo: 75% 
	 */
	public static final String FREQUENCIA_MINIMA_CERTIFICADOS_CURSOS_EXTENSAO = "2_10900_14";
	
	/**
	 * A frequência mínima necessária para a emissão de certificados cursos eventos  de extensão. Exemplo: 90%
	 */
	public static final String FREQUENCIA_MINIMA_CERTIFICADOS_EVENTOS_EXTENSAO = "2_10900_15";
	
	/**
	 * Quantidade de ano(s) que se pode cadastrar ações retroativas. 
	 */
	public static final String ANO_LIMITE_REGISTRO_ACAO_ANTIGA = "2_10900_16";

	/**
	 * Quantidade de avaliações máximas permitida 
	 */
	public static final String LIMITE_AVALIACOES_PENDENTES = "2_10900_17";
	
	/**
	 * Parâmetro que deve verificar a necessidade do envio do relatório final por parte do discente.
	 */
	public static final String NECESSARIO_ENVIAR_RELATORIO_FINAL = "2_10900_22";

}