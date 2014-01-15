/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Data de Criação: 19/05/2009
 */
package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os parâmetros do módulo
 * de stricto sensu.
 * @author David Pereira
 *
 */
public interface ParametrosStrictoSensu {

	/**
	 * Tempo máximo que é permitido aos programas de pós realizar a solicitação de homologação de alunos de pós stricto. 
	 * Após o fim deste período apenas a PPG pode realizar a solicitação de homologação.
	 */
	public static final String TEMPO_MAXIMO_HOMOLOGACAO_STRICTO = "2_10700_1";

	/** ID (chave primária) dos dados pessoais (tabela pessoa) do Pró-Reitoria de Pós-Graduação. */
	public static final String ID_PESSOA_PRO_REITOR_POS_GRADUACAO = "2_10700_2";

	/**
	 * Valor padrão do prazo máximo de conclusão definidos na RESOLUÇÃO Nº
	 * 072/2004-CONSEPE, Art. 28: três anos para mestrado.
	 */
	public static final String PRAZO_MAXIMO_CONCLUSAO_MESTRADO_ACADEMICO = "2_10700_3";
	
	/**
	 * Valor padrão do prazo máximo de conclusão definidos na RESOLUÇÃO Nº
	 * 072/2004-CONSEPE, Art. 28: três anos para mestrado.
	 */
	public static final String PRAZO_MAXIMO_CONCLUSAO_MESTRADO_PROFISSIONAL = "2_10700_4";
	
	/**
	 * Valor padrão do prazo máximo de conclusão definidos na RESOLUÇÃO Nº
	 * 072/2004-CONSEPE, Art. 28: cinco anos para doutorado.
	 */
	public static final String PRAZO_MAXIMO_CONCLUSAO_DOUTORADO = "2_10700_5";
	
	/**
	 * Número máximo de disciplinas que alunos especiais pode pagar.
	 */
	public static final String MAXIMO_DISCIPLINAS_ALUNO_ESPECIAL_POS = "2_10700_6";
	
	/**
	 * Define o início da sequência de Registro de Diplomas no SIGAA para os cursos de Stricto Sensu.
	 */
	public static final String NUMERO_INICIAL_REGISTRO_DIPLOMA = "2_10700_7";
	
	/**
	 * Prazo (em dias) de cadastro da banca a partir da sua execução.
	 */
	public static final String PRAZO_MAXIMO_CADASTRO_BANCA = "2_10700_8";
	
	/**
	 * Indica a quantidade mínima de Atividades do Tipo Proficiência 
	 * que o Aluno de DOUTORADO deve cursar antes de realizar a Defesa.  
	 */
	public static final String QUANTIDADE_MINIMA_PROFICIENCIA_DOUTORADO = "2_10700_9";
	
	/**
	 * Indica a quantidade mínima de Atividades do Tipo Proficiência 
	 * que o Aluno de MESTRADO deve cursar antes de realizar a Defesa.  
	 */
	public static final String QUANTIDADE_MINIMA_PROFICIENCIA_MESTRADO = "2_10700_10";
	
	/**
	 * Quantidade de Dias para iniciar a verificação de alunos que não realizaram a matrícula OnLine.
	 * Verificação realizada no Timer NotificacaoMatriculaOnLIneTimer. 
	 */
	public static final String QUANTIDADE_DIAS_VERIFICACAO_MATRICULA_ONLINE = "2_10700_12";	
	
	/**
	 * Indica se os códigos dos componentes curriculares serão validados conforme sua composição literal.
	 */
	public static final String VALIDAR_CODIGO_COMPONENTE_CURRICULAR = "2_10700_13";
	
	/** Define se o componente curricular da tese de dissertação será definida como disciplina. */
	public static final String TESE_DEFINIDA_COMO_DISCIPLINA = "2_10700_19";
	
	/** Tipo de ciclo de Formação único (Um ciclo). */
	public static final String CICLO_UNICO = "2_10700_20";
	
	/** E-mails dos Responsáveis da Pro-Reitoria de Pós-Graduação. */
	public static final String EMAILS_RESPONSAVEIS_PRO_REITORIA_POS = "2_10700_21";	
	
	
	/** Sigla da gestora principal de Pós-Graduação  */
	public static final String SIGLA_PRO_REITORIA_POS = "2_10700_22";
	
	/** Sigla - Nome da gestora principal de Pós-Graduação  */
	public static final String SIGLA_NOME_PRO_REITORIA_POS = "2_10700_23";
	
	/** Indica o percentual mínimo que o discente deve cumprir em uma determinada disciplina para poder tranca-la. */
	public static final String PERCENTUAL_MIN_DECORRIDO_TRANCAMENTO_DISCIPLINA = "2_10700_24";
	
	/** Verifica se o cadastro de discente de stricto, do tipo regular,
	 *  será considerado em relação ao número de vagas disponíveis no processo seletivo.*/
	public static final String CADASTRAR_DISCENTE_POR_NUMERO_VAGAS = "2_10700_25";
	
	/**
	 * Indica se vai ser permitido a alteração da Grade de Horários para o nível de ensino.
	 */
	public static final String PERMITE_ESCOLHA_GRADE_HORARIOS = "2_10700_27";
	
	/**
	 * Ano de início utilizado para o cadastro de componentes no histórico de discentes antigos.
	 */
	public static final String ANO_INICIO_MATRICULA_IMPLANTACAO_HISTORICO_STRICTO = "2_10700_28";
	
	/** Indica o percentual máximo que o discente pode cumprir em uma determinada disciplina para poder tranca-la. */
	public static final String LIMITE_MAXIMO_SEMESTRE_POSSIBILITA_TRANCAMENTO = "2_10700_29";
	
	/** Nome da Pró-Reitoria de Pós-Graduação. */
	public static final String NOME_PRO_REITORIA_POS_GRADUACAO = "2_10700_30";
	
	/** Telefone e e-mail da Nome da Pró-Reitoria de Pós-Graduação. */
	public static final String CONTATO_PRO_REITORIA_POS_GRADUACAO = "2_10700_31";

	/** Este parâmetro indica se o programa de pós pode realizar o cadastro de discentes antigos. */
	public static final String PERMITE_PROGRAMA_POS_CADASTRAR_DISCENTE_ANTIGO = "2_10700_32";
	
	/** Este parâmetro indica se o programa de pós pode realizar o caso de uso de implantação de histórico de discente */
	public static final String PERMITE_PROGRAMA_POS_IMPLANTAR_HISTORICO_DISCENTE_ANTIGO = "2_10700_33";
	
	/** Este parâmetro indica se é obrigatório informar o processo seletivo no cadastro de discentes de pós-graduação */
	public static final String OBRIGATORIO_INFORMAR_PROCESSO_SELETIVO_CADASTRO_DISCENTE = "2_10700_34";
	
	/** Este parâmetro indica se o coordenador do programa de pós pode realizar a matrícula de discentes. 
		Os valores permitidos são: R - Regular, E - Especial, T - Todos, N - Nenhum */
	public static final String PERMITE_COORDENACAO_MATRICULAR_ALUNO = "2_10700_35";	
	
	/** Este parâmetro indica quais operações sobre a a estrutura curricular o coordenador do programa de 
	 * pós pode realizar. C = CADASTRAR, A = ALTERAR, + = ATIVAR, - = DESATIVAR. Por exemplo, se o valor do parâmetro for
	 * A+- ou qualquer permutação desses simbolos, o coordenador de pós vai poder alterar, ativar e desativar uma estrutura curricular,
	 * mas não vai poder cadastrar */
	public static final String PERMITE_COORDENACAO_CADASTRAR_ALTERAR_ATIVAR_INATIVAR_ESTRUTURA_CURRICULAR = "2_10700_36";
	
	
	/**Este parâmetro indica quais operações sobre componente curricular o coordenador do programa de 
	 * pós pode realizar. C = CADASTRAR, A = ALTERAR. Por exemplo, se o valor do parâmetro for A, 
	 * o coordenador de pós vai poder alterar o componente curricular, mas não vai poder cadastrar.
	 * Se o valor do parâmetro for CA ou qualquer permutação desses símbolos, o coordenador vai
	 * poder alterar e cadastrar o componente curricular.	  
	 */
	public static final String PERMITE_COORDENACAO_CADASTRAR_ALTERAR_COMPONENTE_CURRICULAR = "2_10700_37";
	
	/**
	 * Indica se os calendários dos programas de pós-graduação podem ou não ser definidos por curso.
	 */
	public static final String CALENDARIO_POR_CURSO = "2_10700_38";
}
