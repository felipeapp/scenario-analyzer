/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Data de Cria��o: 19/05/2009
 */
package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os par�metros do m�dulo
 * de stricto sensu.
 * @author David Pereira
 *
 */
public interface ParametrosStrictoSensu {

	/**
	 * Tempo m�ximo que � permitido aos programas de p�s realizar a solicita��o de homologa��o de alunos de p�s stricto. 
	 * Ap�s o fim deste per�odo apenas a PPG pode realizar a solicita��o de homologa��o.
	 */
	public static final String TEMPO_MAXIMO_HOMOLOGACAO_STRICTO = "2_10700_1";

	/** ID (chave prim�ria) dos dados pessoais (tabela pessoa) do Pr�-Reitoria de P�s-Gradua��o. */
	public static final String ID_PESSOA_PRO_REITOR_POS_GRADUACAO = "2_10700_2";

	/**
	 * Valor padr�o do prazo m�ximo de conclus�o definidos na RESOLU��O N�
	 * 072/2004-CONSEPE, Art. 28: tr�s anos para mestrado.
	 */
	public static final String PRAZO_MAXIMO_CONCLUSAO_MESTRADO_ACADEMICO = "2_10700_3";
	
	/**
	 * Valor padr�o do prazo m�ximo de conclus�o definidos na RESOLU��O N�
	 * 072/2004-CONSEPE, Art. 28: tr�s anos para mestrado.
	 */
	public static final String PRAZO_MAXIMO_CONCLUSAO_MESTRADO_PROFISSIONAL = "2_10700_4";
	
	/**
	 * Valor padr�o do prazo m�ximo de conclus�o definidos na RESOLU��O N�
	 * 072/2004-CONSEPE, Art. 28: cinco anos para doutorado.
	 */
	public static final String PRAZO_MAXIMO_CONCLUSAO_DOUTORADO = "2_10700_5";
	
	/**
	 * N�mero m�ximo de disciplinas que alunos especiais pode pagar.
	 */
	public static final String MAXIMO_DISCIPLINAS_ALUNO_ESPECIAL_POS = "2_10700_6";
	
	/**
	 * Define o in�cio da sequ�ncia de Registro de Diplomas no SIGAA para os cursos de Stricto Sensu.
	 */
	public static final String NUMERO_INICIAL_REGISTRO_DIPLOMA = "2_10700_7";
	
	/**
	 * Prazo (em dias) de cadastro da banca a partir da sua execu��o.
	 */
	public static final String PRAZO_MAXIMO_CADASTRO_BANCA = "2_10700_8";
	
	/**
	 * Indica a quantidade m�nima de Atividades do Tipo Profici�ncia 
	 * que o Aluno de DOUTORADO deve cursar antes de realizar a Defesa.  
	 */
	public static final String QUANTIDADE_MINIMA_PROFICIENCIA_DOUTORADO = "2_10700_9";
	
	/**
	 * Indica a quantidade m�nima de Atividades do Tipo Profici�ncia 
	 * que o Aluno de MESTRADO deve cursar antes de realizar a Defesa.  
	 */
	public static final String QUANTIDADE_MINIMA_PROFICIENCIA_MESTRADO = "2_10700_10";
	
	/**
	 * Quantidade de Dias para iniciar a verifica��o de alunos que n�o realizaram a matr�cula OnLine.
	 * Verifica��o realizada no Timer NotificacaoMatriculaOnLIneTimer. 
	 */
	public static final String QUANTIDADE_DIAS_VERIFICACAO_MATRICULA_ONLINE = "2_10700_12";	
	
	/**
	 * Indica se os c�digos dos componentes curriculares ser�o validados conforme sua composi��o literal.
	 */
	public static final String VALIDAR_CODIGO_COMPONENTE_CURRICULAR = "2_10700_13";
	
	/** Define se o componente curricular da tese de disserta��o ser� definida como disciplina. */
	public static final String TESE_DEFINIDA_COMO_DISCIPLINA = "2_10700_19";
	
	/** Tipo de ciclo de Forma��o �nico (Um ciclo). */
	public static final String CICLO_UNICO = "2_10700_20";
	
	/** E-mails dos Respons�veis da Pro-Reitoria de P�s-Gradua��o. */
	public static final String EMAILS_RESPONSAVEIS_PRO_REITORIA_POS = "2_10700_21";	
	
	
	/** Sigla da gestora principal de P�s-Gradua��o  */
	public static final String SIGLA_PRO_REITORIA_POS = "2_10700_22";
	
	/** Sigla - Nome da gestora principal de P�s-Gradua��o  */
	public static final String SIGLA_NOME_PRO_REITORIA_POS = "2_10700_23";
	
	/** Indica o percentual m�nimo que o discente deve cumprir em uma determinada disciplina para poder tranca-la. */
	public static final String PERCENTUAL_MIN_DECORRIDO_TRANCAMENTO_DISCIPLINA = "2_10700_24";
	
	/** Verifica se o cadastro de discente de stricto, do tipo regular,
	 *  ser� considerado em rela��o ao n�mero de vagas dispon�veis no processo seletivo.*/
	public static final String CADASTRAR_DISCENTE_POR_NUMERO_VAGAS = "2_10700_25";
	
	/**
	 * Indica se vai ser permitido a altera��o da Grade de Hor�rios para o n�vel de ensino.
	 */
	public static final String PERMITE_ESCOLHA_GRADE_HORARIOS = "2_10700_27";
	
	/**
	 * Ano de in�cio utilizado para o cadastro de componentes no hist�rico de discentes antigos.
	 */
	public static final String ANO_INICIO_MATRICULA_IMPLANTACAO_HISTORICO_STRICTO = "2_10700_28";
	
	/** Indica o percentual m�ximo que o discente pode cumprir em uma determinada disciplina para poder tranca-la. */
	public static final String LIMITE_MAXIMO_SEMESTRE_POSSIBILITA_TRANCAMENTO = "2_10700_29";
	
	/** Nome da Pr�-Reitoria de P�s-Gradua��o. */
	public static final String NOME_PRO_REITORIA_POS_GRADUACAO = "2_10700_30";
	
	/** Telefone e e-mail da Nome da Pr�-Reitoria de P�s-Gradua��o. */
	public static final String CONTATO_PRO_REITORIA_POS_GRADUACAO = "2_10700_31";

	/** Este par�metro indica se o programa de p�s pode realizar o cadastro de discentes antigos. */
	public static final String PERMITE_PROGRAMA_POS_CADASTRAR_DISCENTE_ANTIGO = "2_10700_32";
	
	/** Este par�metro indica se o programa de p�s pode realizar o caso de uso de implanta��o de hist�rico de discente */
	public static final String PERMITE_PROGRAMA_POS_IMPLANTAR_HISTORICO_DISCENTE_ANTIGO = "2_10700_33";
	
	/** Este par�metro indica se � obrigat�rio informar o processo seletivo no cadastro de discentes de p�s-gradua��o */
	public static final String OBRIGATORIO_INFORMAR_PROCESSO_SELETIVO_CADASTRO_DISCENTE = "2_10700_34";
	
	/** Este par�metro indica se o coordenador do programa de p�s pode realizar a matr�cula de discentes. 
		Os valores permitidos s�o: R - Regular, E - Especial, T - Todos, N - Nenhum */
	public static final String PERMITE_COORDENACAO_MATRICULAR_ALUNO = "2_10700_35";	
	
	/** Este par�metro indica quais opera��es sobre a a estrutura curricular o coordenador do programa de 
	 * p�s pode realizar. C = CADASTRAR, A = ALTERAR, + = ATIVAR, - = DESATIVAR. Por exemplo, se o valor do par�metro for
	 * A+- ou qualquer permuta��o desses simbolos, o coordenador de p�s vai poder alterar, ativar e desativar uma estrutura curricular,
	 * mas n�o vai poder cadastrar */
	public static final String PERMITE_COORDENACAO_CADASTRAR_ALTERAR_ATIVAR_INATIVAR_ESTRUTURA_CURRICULAR = "2_10700_36";
	
	
	/**Este par�metro indica quais opera��es sobre componente curricular o coordenador do programa de 
	 * p�s pode realizar. C = CADASTRAR, A = ALTERAR. Por exemplo, se o valor do par�metro for A, 
	 * o coordenador de p�s vai poder alterar o componente curricular, mas n�o vai poder cadastrar.
	 * Se o valor do par�metro for CA ou qualquer permuta��o desses s�mbolos, o coordenador vai
	 * poder alterar e cadastrar o componente curricular.	  
	 */
	public static final String PERMITE_COORDENACAO_CADASTRAR_ALTERAR_COMPONENTE_CURRICULAR = "2_10700_37";
	
	/**
	 * Indica se os calend�rios dos programas de p�s-gradua��o podem ou n�o ser definidos por curso.
	 */
	public static final String CALENDARIO_POR_CURSO = "2_10700_38";
}
