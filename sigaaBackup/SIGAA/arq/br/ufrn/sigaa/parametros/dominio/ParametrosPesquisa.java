/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 19/05/2009
 */
package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os parâmetros do módulo
 * de pesquisa.
 * @author David Pereira
 *
 */
public interface ParametrosPesquisa {

	/*********************
	 * Módulo de Pesquisa
	 **********************/
	
	/** Número de horas de tolerâncias após o fim do prazo de submissão de projetos */
	public static final String HORAS_TOLERANCIA_SUBMISSAO = "2_10800_1";
	
	/** Duração máxima, em meses, para novos projetos*/
	public static final String DURACAO_MAXIMA_NOVOS_PROJETOS = "2_10800_2";
	
	/** Limite de cotas de bolsas solicitadas por projeto*/
	public static final String LIMITE_COTAS_PROJETO = "2_10800_3";
	
	/** Limite de cotas de bolsas por orientador*/
	public static final String LIMITE_COTAS_ORIENTADOR = "2_10800_4";
	/* Prazos */
	
	/** Início da submissão de projetos*/
	public static final String INICIO_SUBMISSAO_PROJETOS_PESQUISA = "2_10800_5";
	
	/** Fim da submissão de projetos*/
	public static final String FIM_SUBMISSAO_PROJETOS_PESQUISA = "2_10800_6";
	
	/** Início da submissão de relatórios parciais de discentes*/
	public static final String INICIO_SUBMISSAO_RELATORIO_PARCIAL = "2_10800_7";
	
	/** Fim da submissão de relatórios parciais de discentes*/
	public static final String FIM_SUBMISSAO_RELATORIO_PARCIAL = "2_10800_8";
	
	/**Numero máximo de vezes que um projeto de pesquisa pode ser renovado*/
	public static final String NUMERO_MAXIMO_RENOVACOES_PROJETO = "2_10800_9";
	
	/** Template do email de notificação dos consultores*/
	public static final String TEMPLATE_NOTIFICACAO_CONSULTOR = "2_10800_10";
	
	/** Período Inicial de avaliação dos consultores*/
	public static final String INICIO_PERIODO_CONSULTORIA = "2_10800_11";
	
	/** Período Final de avaliação dos consultores*/
	public static final String FIM_PERIODO_CONSULTORIA = "2_10800_12";
	
	/** Dia limite para alterações de bolsistas efetivarem-se no mês corrente*/
	public static final String LIMITE_ALTERACAO_BOLSISTA = "2_10800_13";
	
	/** Email para recebimento de notificação de alterações de bolsistas*/
	public static final String EMAIL_NOTIFICACAO_BOLSISTA = "2_10800_14";
	
	/** Email para recebimento de notificações de invenção*/
	public static final String EMAIL_NOTIFICACAO_INVENCAO = "2_10800_15";
	
	/** Id da unidade Pró-Reitoria de Pesquisa nos Sistemas*/
	public static final String ID_UNIDADE_PROPESQ = "2_10800_16";
	
	/** Id do tipo de processo NOTIFICAÇÃO DE INVENÇÃO no sistema de protocolo*/ 
	public static final String ID_TIPO_PROCESSO_NOTIFICACAO_INVENCAO = "2_10800_17";
	
	/** Id do tipo de interessado SERVIDOR no sistema de protocolo*/
	public static final String ID_TIPO_INTERESSADO_SERVIDOR = "2_10800_18";

	/** Id da unidade Núcleo de Inovação Tecnológica nos Sistemas*/
	public static final String ID_UNIDADE_NIT = "2_10800_19";
	
	/** Permite envio de relatórios parciais pelos alunos de iniciação científica? (Sim/Não) */
	public static final String ENVIO_RELATORIO_PARCIAL = "2_10800_20";
	
	/** Permite envio de resumos do CIC independentes? (Sim/Não) */
	public static final String ENVIO_RESUMO_INDEPENDENTE = "2_10800_21";

	/** Sigla da gestora principal de Pesquisa  */
	public static final String SIGLA_PRO_REITORIA_PESQUISA = "2_10800_22";
	
	/** Sigla - Nome da gestora principal de Pesquisa  */
	public static final String SIGLA_NOME_PRO_REITORIA_PESQUISA = "2_10800_23";
	
	/** Indica a classe que implementa os comportamentos personalizáveis do módulo de pesquisa */
	public static final String IMPLEMENTACAO_COMPORTAMENTOS_PESQUISA = "2_10800_24";
	
	/** Nome do pro-reitor(a) de pesquisa */
	public static final String NOME_PRO_REITOR_PESQUISA = "2_10800_25";
	
	/** Id do tipo de processo BASE DE PESQUISA no sistema de protocolo*/ 
	public static final String ID_TIPO_PROCESSO_GRUPO_PESQUISA = "2_10800_26";
	
	/** Quantidade máxima de caracteres na submissão de uma grupo de pesquisa */
	public static final String NUMERO_CARACTERES_GRUPO_PESQUISA = "2_10800_27";

	/** Valor máximo permito para o orçamento de uma projeto de apoio para um novo pesquisador */
	public static final String VALOR_MAXIMO_ORCAMENTO_PROJ_APOIO_NOVOS_PESQUISADORES = "2_10800_28";

	/** Quantidade em MESES para ser considerado apto a concorrer no edital de novos pesquisadores */
	public static final String QNT_MESES_NOVOS_PESQUISADORES = "2_10800_29";

	/** Valor máximo permitido para o orçamento de uma projeto de apoio hpá um grupo de pesquisa */
	public static final String VALOR_MAXIMO_ORCAMENTO_PROJ_APOIO_GRUPO_PESQUISA = "2_10800_30";
	
	/** Quantidade de dias contados a partir do início do projeto de pesquisa a partir do qual é permitido o envio de relatório anual. */
	public static final String DIAS_PARA_ENVIO_RELATORIO_ANUAL_PROJETO_PESQUISA = "2_10800_31";

	/** Id da entidade Financiadora CNPQ */
	public static final String ID_ENTIDADE_FINANCIADORA_CNPQ = "2_10800_32";
	
	/** Indica se o sistema utilizará a data de admissão do servidor docente ao invés da
	 *  data de término da formação de doutorado na validação para submissão de projetos de apoio a novos pesquisadores. */
	public static final String UTILIZA_DATA_ADMISSAO_APOIO_NOVOS_PESQUISADORES = "2_10800_33";
	
	/** Quantidade máxima de caracteres permitida no resumo expandido do relatório de projetos de pesquisa. */
	public static final String MAXIMO_CARACTERES_RELATORIO_PROJETO = "2_10800_34";
	
	/** Quantidade mínima de linhas de pesquisa que deve ser informada na proposta de criação de um novo grupo de pesquisa. */
	public static final String MINIMO_LINHAS_NOVO_GRUPO_PESQUISA = "2_10800_35";

	/** Limite de cotas de bolsas por orientador Externo */
	public static final String LIMITE_COTAS_ORIENTADOR_EXTERNO = "2_10800_36";

	/** Endereço de email de Reply-To da notificação dos consultores. */
	public static final String EMAIL_REPLY_TO_NOTIFICACAO_CONSULTOR = "2_10800_37";
}