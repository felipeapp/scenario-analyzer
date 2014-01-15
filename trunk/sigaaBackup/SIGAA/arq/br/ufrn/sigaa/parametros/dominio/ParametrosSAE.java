package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os parâmetros do módulo
 * do SAE.
 * 
 * @author Henrique André
 *
 */
public interface ParametrosSAE {

	/** Serve para armanezar o valor do salário mínimo */
	public static final String SALARIO_MINIMO = "2_11300_1";
	
	/** Armazena o valor da bolsa de Residência de Graduação */
	public static final String RESIDENCIA_GRADUACAO = "2_11300_2";

	/** Armazena o valor da bolsa de Residência de Pós Graduação */
	public static final String RESIDENCIA_POS = "2_11300_3";
	
	/** Armazena o valor da bolsa de Alimentação */
	public static final String ALIMENTACAO = "2_11300_4";

	/** Armazena o valor da bolsa de Transporte */
	public static final String TRANSPORTE = "2_11300_5";

	/** Armazena o valor da bolsa do PROMISAES */
	public static final String PROMISAES = "2_11300_17";

	/** Armazena o valor da bolsa do Creche */
	public static final String CRECHE = "2_11300_18";

	/** Armazena o valor da bolsa do Atleta */
	public static final String ATLETA = "2_11300_19";
	
	/** Armazena o valor da bolsa do Creche */
	public static final String OCULOS = "2_11300_20";

	/** Armazena o valor da bolsa respectivas no sipac */
	public static final String BOLSAS_ESPECIFICAS_SIPAC = "2_11300_10";
	
	/** Unidade que será setada na bolsa quando a mesma for contemplada pelo SAE e integrada ao SIPAC. */
	public static final String UNIDADE_SOLICITACAO_BOLSA_SAE = "2_11300_11";
	
	/** Armezena o valor da situação da bolsa do discente ao solicitar */
	public static final String EM_ANALISE = "2_11300_6";
	
	/** Armezena o valor da situação da bolsa do discente ao ser aceita */
	public static final String BOLSA_DEFERIDA_E_CONTEMPLADA = "2_11300_7";
	
	/** Armezena o valor da situação da bolsa do discente ao entrar na fila de espera */
	public static final String BOLSA_DEFERIDA_FILA_DE_ESPERA = "2_11300_8";
	
	/** Armezena o valor da situação da bolsa do discente ao ser negada */
	public static final String BOLSA_INDEFERIDA = "2_11300_9";
	
	/** Armezena o valor da situação da bolsa do discente ao ser cancelada */
	public static final String BOLSA_CANCELADA = "2_11300_14";
	
	/** Armezena o valor da situação da bolsa do discente ao ser contemplada para alimentação e ser 
	 * colocada em espera para residência */
	public static final String BOLSA_ALIMENTACAO_DEFERIDA_CONTEMP_BOLSA_MORADIA_EM_ESPERA = "2_11300_15";
	
	/** Dia inicial (de qualquer mês) do período permitido para cadastro de interesse em bolsas de apoio técnico. */
	public static final String DIA_INICIAL_REGISTRO_INTERESSE_BOLSA_APOIO_TECNICO = "2_11300_12";
	
	/** Dia final (de qualquer mês) do período permitido para cadastro de interesse em bolsas de apoio técnico. */
	public static final String DIA_FINAL_REGISTRO_INTERESSE_BOLSA_APOIO_TECNICO = "2_11300_13";

	/** Se deve ser preechido os dias padrões de bolsa auxilio */
	public static final String DEFINICAO_DIAS_PADROES_BOLSA_AUXILIO = "2_11300_16";

	/** Armezena o valor da situação da bolsa do discente ao ser finalizada */
	public static final String BOLSA_FINALIZADA = "2_11300_21";
	
	/** Id da unidade Pró-Reitoria de Assistencia ao Estudante*/
	public static final String ID_UNIDADE_PROAE = "2_11300_22";
	
	/** Parâmetro multivalorado que armazena os endereços de e-mail dos interessados da PROAE separados por ponto-e-vírgula */
	public static final String EMAIL_PROAE = "2_11300_23";
	
	/** Prazo para abandonar a Residência depois de colado grau */
	public static final String PRAZO_PARA_ABANDONAR_RESIDENCIA = "2_11300_24";

	/** Verifica a necessidade de adesão ao cadastro único para solicitação de bolsa */
	public static final String NECESSIDADE_ADESAO_CADASTRO_UNICO = "2_11300_25";
	
	/** Armezena o valor da situação da bolsa do discente ao ser pendente de renovacao */
	public static final String BOLSA_SOLICITADA_RENOVACAO = "2_11300_26";

}