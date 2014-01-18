/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Cria��o: 19/05/2009
 */
package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os par�metros do m�dulo
 * de extens�o.
 * @author David Pereira
 *
 */
public interface ParametrosExtensao {

	/**
	 * Lista (com elementos separados por v�rgula) dos IDs de tipos de bolsas de extens�o
	 */
	public static final String LISTA_BOLSAS_EXTENSAO = "2_10900_1";
	
	/**
	 * Nome do Pr�-Reitor de extens�o. Utilizado nos certificados.
	 */
	public static final String NOME_PRO_REITOR_EXTENSAO = "2_10900_2";
	
	/**
	 * N�mero do telefone da pr�-reitoria de extens�o. Utilizado nos certificados.
	 */
	public static final String TELEFONE_PRO_REITORIA_EXTENSAO = "2_10900_18";
	
	/**
	 * N�mero do fax da pr�-reitoria de extens�o. Utilizado nos certificados.
	 */
	public static final String FAX_PRO_REITORIA_EXTENSAO = "2_10900_19";
	
	/**
	 * Endere�o do e-mail da pr�-reitoria de extens�o. Utilizado nos certificados.
	 */
	public static final String EMAIL_PRO_REITORIA_EXTENSAO = "2_10900_20";
	
	/**
	 * Endere�o do sitio da pr�-reitoria de extens�o. Utilizado nos certificados.
	 */
	public static final String SITIO_PRO_REITORIA_EXTENSAO = "2_10900_21";
	
	/**
	 * Total de a��es ativas que um docente pode Coordenar simultaneamente.
	 */
	public static final String TOTAL_ACOES_ATIVAS_MESMA_MODALIDADE_COORDENADAS = "2_10900_3";
	
	/**
	 * Quantidade m�xima de atraso do Relat�rio Final ap�s o t�rmino de uma A��o de Extens�o. 
	 */
	public static final String TOTAL_MAXIMO_DIAS_PERMITIDO_ATRASO_RELATORIO = "2_10900_4";
	
	/**
	 * Identificador da bolsa do fundo de apoio de extens�o no SIPAC. 
	 */
	public static final String BOLSA_EXTENSAO = "2_10900_5";
	
	/**
	 * Identificador de quantos dias a data fim da a��o pode ser ultrapassada.
	 */
	public static final String DIAS_ENCERRAR_ACOES_PENDENCIAS_EXECUCAO = "2_10900_7";
	
	/**
	 * Sigla do Fundo de Extens�o Padr�o.
	 */
	public static final String SIGLA_FUNDO_EXTENSAO_PADRAO = "2_10900_8";
	
	/**
	 * Nome do Fundo de Extens�o Padr�o.
	 */
	public static final String NOME_FUNDO_EXTENSAO_PADRAO = "2_10900_9";

	/**
	 * Respons�vel pela exibi��o do link para avalia��o final de proposta.
	 */
	public static final String EXIBIR_AVALIACAO_FINAL_PROPOSTA = "2_10900_10";
	
	/**
	 * Respons�vel pela verifica��o da data limite de altera��o da Carga hor�ria da Equipe Executora
	 */
	public static final String DATA_LIMITE_ALTERACAO_CH_EQUIPE_EXECUTORA = "2_10900_11";
	
	/**
	 * Dia inicial de indica��o de bolsista para os planos de trabalho, para entrar na folha de pagamento do m�s corrente
	 */
	public static final String DIA_INICIAL_INDICACAO_BOLSISTA = "2_10900_12";

	/**
	 * Dia final de indica��o de bolsista para os planos de trabalho, para entrar na folha de pagamento do m�s corrente
	 */
	public static final String DIA_FINAL_INDICACAO_BOLSISTA = "2_10900_13";
	
	
	/**
	 * A frequ�ncia m�nima necess�ria para a emiss�o de certificados para cursos de extens�o.  Exemplo: 75% 
	 */
	public static final String FREQUENCIA_MINIMA_CERTIFICADOS_CURSOS_EXTENSAO = "2_10900_14";
	
	/**
	 * A frequ�ncia m�nima necess�ria para a emiss�o de certificados cursos eventos  de extens�o. Exemplo: 90%
	 */
	public static final String FREQUENCIA_MINIMA_CERTIFICADOS_EVENTOS_EXTENSAO = "2_10900_15";
	
	/**
	 * Quantidade de ano(s) que se pode cadastrar a��es retroativas. 
	 */
	public static final String ANO_LIMITE_REGISTRO_ACAO_ANTIGA = "2_10900_16";

	/**
	 * Quantidade de avalia��es m�ximas permitida 
	 */
	public static final String LIMITE_AVALIACOES_PENDENTES = "2_10900_17";
	
	/**
	 * Par�metro que deve verificar a necessidade do envio do relat�rio final por parte do discente.
	 */
	public static final String NECESSARIO_ENVIAR_RELATORIO_FINAL = "2_10900_22";

}