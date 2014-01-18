/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Cria��o: 19/05/2009
 */
package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os par�metros do m�dulo
 * de pesquisa.
 * @author David Pereira
 *
 */
public interface ParametrosPesquisa {

	/*********************
	 * M�dulo de Pesquisa
	 **********************/
	
	/** N�mero de horas de toler�ncias ap�s o fim do prazo de submiss�o de projetos */
	public static final String HORAS_TOLERANCIA_SUBMISSAO = "2_10800_1";
	
	/** Dura��o m�xima, em meses, para novos projetos*/
	public static final String DURACAO_MAXIMA_NOVOS_PROJETOS = "2_10800_2";
	
	/** Limite de cotas de bolsas solicitadas por projeto*/
	public static final String LIMITE_COTAS_PROJETO = "2_10800_3";
	
	/** Limite de cotas de bolsas por orientador*/
	public static final String LIMITE_COTAS_ORIENTADOR = "2_10800_4";
	/* Prazos */
	
	/** In�cio da submiss�o de projetos*/
	public static final String INICIO_SUBMISSAO_PROJETOS_PESQUISA = "2_10800_5";
	
	/** Fim da submiss�o de projetos*/
	public static final String FIM_SUBMISSAO_PROJETOS_PESQUISA = "2_10800_6";
	
	/** In�cio da submiss�o de relat�rios parciais de discentes*/
	public static final String INICIO_SUBMISSAO_RELATORIO_PARCIAL = "2_10800_7";
	
	/** Fim da submiss�o de relat�rios parciais de discentes*/
	public static final String FIM_SUBMISSAO_RELATORIO_PARCIAL = "2_10800_8";
	
	/**Numero m�ximo de vezes que um projeto de pesquisa pode ser renovado*/
	public static final String NUMERO_MAXIMO_RENOVACOES_PROJETO = "2_10800_9";
	
	/** Template do email de notifica��o dos consultores*/
	public static final String TEMPLATE_NOTIFICACAO_CONSULTOR = "2_10800_10";
	
	/** Per�odo Inicial de avalia��o dos consultores*/
	public static final String INICIO_PERIODO_CONSULTORIA = "2_10800_11";
	
	/** Per�odo Final de avalia��o dos consultores*/
	public static final String FIM_PERIODO_CONSULTORIA = "2_10800_12";
	
	/** Dia limite para altera��es de bolsistas efetivarem-se no m�s corrente*/
	public static final String LIMITE_ALTERACAO_BOLSISTA = "2_10800_13";
	
	/** Email para recebimento de notifica��o de altera��es de bolsistas*/
	public static final String EMAIL_NOTIFICACAO_BOLSISTA = "2_10800_14";
	
	/** Email para recebimento de notifica��es de inven��o*/
	public static final String EMAIL_NOTIFICACAO_INVENCAO = "2_10800_15";
	
	/** Id da unidade Pr�-Reitoria de Pesquisa nos Sistemas*/
	public static final String ID_UNIDADE_PROPESQ = "2_10800_16";
	
	/** Id do tipo de processo NOTIFICA��O DE INVEN��O no sistema de protocolo*/ 
	public static final String ID_TIPO_PROCESSO_NOTIFICACAO_INVENCAO = "2_10800_17";
	
	/** Id do tipo de interessado SERVIDOR no sistema de protocolo*/
	public static final String ID_TIPO_INTERESSADO_SERVIDOR = "2_10800_18";

	/** Id da unidade N�cleo de Inova��o Tecnol�gica nos Sistemas*/
	public static final String ID_UNIDADE_NIT = "2_10800_19";
	
	/** Permite envio de relat�rios parciais pelos alunos de inicia��o cient�fica? (Sim/N�o) */
	public static final String ENVIO_RELATORIO_PARCIAL = "2_10800_20";
	
	/** Permite envio de resumos do CIC independentes? (Sim/N�o) */
	public static final String ENVIO_RESUMO_INDEPENDENTE = "2_10800_21";

	/** Sigla da gestora principal de Pesquisa  */
	public static final String SIGLA_PRO_REITORIA_PESQUISA = "2_10800_22";
	
	/** Sigla - Nome da gestora principal de Pesquisa  */
	public static final String SIGLA_NOME_PRO_REITORIA_PESQUISA = "2_10800_23";
	
	/** Indica a classe que implementa os comportamentos personaliz�veis do m�dulo de pesquisa */
	public static final String IMPLEMENTACAO_COMPORTAMENTOS_PESQUISA = "2_10800_24";
	
	/** Nome do pro-reitor(a) de pesquisa */
	public static final String NOME_PRO_REITOR_PESQUISA = "2_10800_25";
	
	/** Id do tipo de processo BASE DE PESQUISA no sistema de protocolo*/ 
	public static final String ID_TIPO_PROCESSO_GRUPO_PESQUISA = "2_10800_26";
	
	/** Quantidade m�xima de caracteres na submiss�o de uma grupo de pesquisa */
	public static final String NUMERO_CARACTERES_GRUPO_PESQUISA = "2_10800_27";

	/** Valor m�ximo permito para o or�amento de uma projeto de apoio para um novo pesquisador */
	public static final String VALOR_MAXIMO_ORCAMENTO_PROJ_APOIO_NOVOS_PESQUISADORES = "2_10800_28";

	/** Quantidade em MESES para ser considerado apto a concorrer no edital de novos pesquisadores */
	public static final String QNT_MESES_NOVOS_PESQUISADORES = "2_10800_29";

	/** Valor m�ximo permitido para o or�amento de uma projeto de apoio hp� um grupo de pesquisa */
	public static final String VALOR_MAXIMO_ORCAMENTO_PROJ_APOIO_GRUPO_PESQUISA = "2_10800_30";
	
	/** Quantidade de dias contados a partir do in�cio do projeto de pesquisa a partir do qual � permitido o envio de relat�rio anual. */
	public static final String DIAS_PARA_ENVIO_RELATORIO_ANUAL_PROJETO_PESQUISA = "2_10800_31";

	/** Id da entidade Financiadora CNPQ */
	public static final String ID_ENTIDADE_FINANCIADORA_CNPQ = "2_10800_32";
	
	/** Indica se o sistema utilizar� a data de admiss�o do servidor docente ao inv�s da
	 *  data de t�rmino da forma��o de doutorado na valida��o para submiss�o de projetos de apoio a novos pesquisadores. */
	public static final String UTILIZA_DATA_ADMISSAO_APOIO_NOVOS_PESQUISADORES = "2_10800_33";
	
	/** Quantidade m�xima de caracteres permitida no resumo expandido do relat�rio de projetos de pesquisa. */
	public static final String MAXIMO_CARACTERES_RELATORIO_PROJETO = "2_10800_34";
	
	/** Quantidade m�nima de linhas de pesquisa que deve ser informada na proposta de cria��o de um novo grupo de pesquisa. */
	public static final String MINIMO_LINHAS_NOVO_GRUPO_PESQUISA = "2_10800_35";

	/** Limite de cotas de bolsas por orientador Externo */
	public static final String LIMITE_COTAS_ORIENTADOR_EXTERNO = "2_10800_36";

	/** Endere�o de email de Reply-To da notifica��o dos consultores. */
	public static final String EMAIL_REPLY_TO_NOTIFICACAO_CONSULTOR = "2_10800_37";
}