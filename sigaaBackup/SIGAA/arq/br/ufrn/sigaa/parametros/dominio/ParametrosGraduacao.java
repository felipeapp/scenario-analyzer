/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on '19/05/2009'
 *
 */
package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os par�metros do m�dulo
 * de gradua��o.
 * @author David Pereira
 * @author wendell
 *
 */
public interface ParametrosGraduacao {

	/**
	 * Data de in�cio da revalida��o de diplomas de gradua��o
	 */
	public static final String INICIO_PERIODO_REVALIDACAO = "2_10500_1";
	
	/**
	 * Data de fim da revalida��o de diplomas de gradua��o
	 */
	public static final String FIM_PERIODO_REVALIDACAO = "2_10500_2";
	
	/**
	 * Ano a ser considerado para valida��es de carga hor�ria complementar de estruturas
	 * curriculares, de acordo com a resolu��o do CONSEPE onde esta valida��o passou a ser exigida
	 */
	public static final String ANO_RESOLUCAO_CONSEPE = "2_10500_3";
	
	/**
	 * Lista de e-mails das pessoas que devem ser notificadas com o resumo das faltas
	 * de docentes informadas pelos alunos
	 */
	public static final String EMAIL_RELATORIO_FALTA_DOCENTE = "2_10500_4";
	
	/** idPessoa do Diretor do DAE, utilizado no registro de diplomas. */
	public static final String ID_PESSOA_DIRETOR_DAE = "2_10500_5";

	/** idPessoa do Diretor da Divis�o de Registro de Diplomas */
	public static final String ID_PESSOA_DIRETOR_DRED = "2_10500_6";
	
	/** Ano de in�cio do registro de diplomas no SIGAA. */
	public static final String ANO_INICIO_REGISTRO_DIPLOMA = "2_10500_7";
	
	/** Semestre de in�cio do registro de diplomas no SIGAA. */
	public static final String SEMESTRE_INICIO_REGISTRO_DIPLOMA = "2_10500_8";
	
	/**
	 * Lista, separada por v�rgula, de c�digos de unidade que dever�o ignorar a sigla utilizada no
	 * cadastro de componentes curriculares. Exemplo, para o Departamento de
	 * Artes poder cadastrar disciplina com o a sigla DGN, infomar o c�digo 1313.
	 */
	public static final String CODIGO_UNIDADES_NAO_VERIFICAR_SIGLA_NO_CADASTRO_DE_COMPONENTES = "2_10500_9";

	/**
	 * Define a CH m�xima de um componente que pode ser oferecida no per�odo letivo de f�rias
	 */
	public static final String CARGA_HORARIA_DISCIPLINA_FERIAS = "2_10500_10";
	
	/**
	 * Define a permiss�o de acesso das coordena��es de cursos de gradua��o para a opera��o de matr�cula em turmas de f�rias. 
	 */
	public static final String ACESSO_COORDENACAO_MATRICULA_TURMA_DE_FERIAS = "2_10500_11";
	
	/**
	 * Define o in�cio da sequ�ncia de Registro de Diplomas no SIGAA para a gradua��o.  
	 */
	public static final String NUMERO_INICIAL_REGISTRO_DIPLOMA = "2_10500_12";
	
	/**
	 * Porcentagem m�nima que define como calcular o perfil inicial do discente.
	 */
	public static final String PERFIL_INICIAL_PORCENTAGEM_MINIMA_CH = "2_10500_13";
	
	/**
	 * Define se no c�lculo do prazo m�ximo, o perfil inicial vai ser considerado.
	 * 
	 * Para alunos a quem seja atribu�do um perfil inicial diferente de 0 (zero), 
	 * o n�mero de n�veis adicionais ser� descontado do n�mero de 
	 * per�odos m�ximo para conclus�o do curso.
	 */
	public static final String CALCULO_PRAZO_FINAL_DEVE_CONSIDERAR_PERFIL_INICIAL = "2_10500_14";
	
	/**
	 * Define a quantidade m�xima de horas por turno que um discente pode ter.
	 */
	public static final String CARGA_HORARIA_AULA_MAXIMA_POR_TURNO = "2_10500_15";
	
	/**
	 * Define a quantidade m�xima de horas por dia que um discente pode ter.
	 */
	public static final String CARGA_HORARIA_AULA_MAXIMA_POR_DIA = "2_10500_16";
	
	/**
	 * Indica o tempo m�dio necess�rio para realizar os c�lculos de um discente
	 * de gradua��o.
	 */
	public static final String TEMPO_MEDIO_CALCULOS_POR_DISCENTE = "2_10500_17";
	
	/**
	 * Indica o Id do curso de Comunica��o Social.
	 */
	public static final String ID_CURSO_COMUNICACAO_SOCIAL = "2_10500_18";
	
	/**
	 * Indica o Id do curso de Psicologia.
	 */
	public static final String ID_CURSO_PSICOLOGIA = "2_10500_19";	
	
	/**
	 * Indica o Id da �rea de Conhecimento CNPJ de Ci�ncias da Sa�de.
	 */
	public static final String ID_AREA_CCS = "2_10500_20";		
	
	/**
	 * Indica a quantidade m�ximas de mudan�as de turno na gradua��o.
	 */
	public static final String QTD_MAXIMA_MUDANCA_TURNO = "2_10500_21";

	/**
	 * Indica �ndice Acad�mico utilizado na elabora��o do relat�rios de alunos Laureados.
	 */
	public static final String INDICE_ACADEMICO_LAUREADOS = "2_10500_24";
	
	/**
	 * Indica o valor m�nimo do �ndice Acad�mico, a ser considerado na elabora��o do relat�rio de alunos laureados. 
	 */
	public static final String VALOR_MINIMO_INDICE_ACADEMICO_LAUREADOS = "2_10500_25";
	
	/**
	 * Indica a quantidade de dias para frente em rela��o ao dia corrente,
	 * considerado na amostragem das atividades (avalia��es e tarefas)
	 * na p�gina principal do Portal do Discente.
	 */
	public static final String QTD_DIAS_FRENTE_ATIVIDADES = "2_10500_27";
	
	/**
	 * Indica a quantidade de dias para tr�s em rela��o ao dia corrente,
	 * considerado na amostragem das atividades (avalia��es e tarefas)
	 * na p�gina principal do Portal do Discente. 
	 */
	public static final String QTD_DIAS_TRAS_ATIVIDADES = "2_10500_28";		
	
	/**
	 * Indica o m�s e ano limite utilizado para definir se um aluno � antigo e inativo,
	 * para realizar o controle de qual c�lculo rendimento escolar ser� exibido no hist�rico do aluno.
	 * Se a data de sa�da deste aluno for menor ou igual a esta data parametrizada ser� exibido o IRA, 
	 * ao inv�s dos �ndices acad�micos.
	 */
	public static final String MES_ANO_LIMITE_ALUNOS_ANTIGOS_INATIVOS = "2_10500_29";		
	
	/**
	 * Indica a quantidade m�nima de dias da realiza��o da prova de reposi��o ap�s confirma��o. 
	 */
	public static final String QUANTIDADE_MIN_DIAS_REALIZACAO_REPOSICAO_PROVA = "2_10500_30";
	
	/**
	 * Indica a quantidade m�xima de dias para solicitar a reposi��o de prova. 
	 */
	public static final String QUANTIDADE_MAXIMA_DIAS_REPOSICAO_PROVA = "2_10500_31";	
	
	/**
	 * Indica a quantidade m�xima de discentes em uma turma de ensino individual. 
	 */
	public static final String QTD_MAX_DISCENTES_TURMA_ENSINO_INDIVIDUAL = "2_10500_32";
	
	/**
	 * Indica o Id do curso de Administra��o � Dist�ncia.
	 */
	public static final String ID_CURSO_ADMINISTRACAO_A_DISTANCIA = "2_10500_33";
	
	/**
	 * Indica se estar� habilitado ou n�o o procedimento de concord�ncia com o regulamento dos cursos de gradua��o
	 * realizado pelos alunos na opera��o de matr�cula on-line.
	 */
	public static final String CONCORDANCIA_REGULAMENTO = "2_10500_35";
	
	/**
	 * Indica a quantidade m�nima de discentes em uma turma de f�rias 
	 */
	public static final String QTD_MIN_DISCENTES_TURMA_FERIAS = "2_10500_36";
	
	/**
	 * Indica se estar� habilitado ou n�o o procedimento de atualiza��o dos dados pessoais pelos discentes durante a matr�cula on-line.
	 */
	public static final String ATUALIZAR_DADOS_PESSOAIS_MATRICULA_ONLINE = "2_10500_37";

	/**
	 * Indica o ano e periodo dos discentes que ser�o incluidos no novo c�lculo do prazo m�ximo
	 */
	public static final String ANO_PERIODO_PERFIL_INICIAL_DIMINUINDO_PRAZO_MAXIMO = "2_10500_38";
	
	
	/**
	 * Sigla - Nome do Departamento de Administra��o Escolar
	 */
	public static final String SIGLA_NOME_ADM_ESCOLAR = "2_10500_39";
	
	/**
	 * Sigla - Nome do setor respons�vel pela gest�o de Gradua��o
	 */
	public static final String SIGLA_NOME_GESTAO_GRADUACAO = "2_10500_40";
	
	/**
	 * Verifica se a Institui��o utilizar� o prazo para limite de trancamento de turmas no m�dulo gradua��o,
	 *  para as solicita��es de trancamento, executadas durante o timer de trancamento.
	 */
	public static final String UTILIZAR_PRAZO_TRANCAMENTO_TURMA = "2_10500_42";
	
	/**
	 * Indica o valor do identificador do �ndice acad�mico MC(M�dia de Conclus�o).
	 */
	public static final String ID_MC = "2_10500_43";
	
	/**
	 * Indica o valor do identificador do �ndice acad�mico MCN(M�dia de Conclus�o Normalizada).
	 */
	public static final String ID_MCN = "2_10500_44";
	
	
	/**
	 * Indica se vai ser permitido a altera��o da Grade de Hor�rios para o n�vel de ensino.
	 */
	public static final String PERMITE_ESCOLHA_GRADE_HORARIOS = "2_10500_45";
	
	/** Taxa de revalida��o de diploma */
	public static final String TAXA_REVALIDACAO_DIPLOMA = "2_10500_47";
	
	/** Descri��o utilizada para a convoca��o da primeira chamada do vestibular. "1� Chamada "*/
	public static final String DESCRICAO_PRIMEIRA_CHAMADA_VESTIBULAR = "2_10500_48";
	
	/** Ano de in�cio da utiliza��o do objeto ConvocacaoProcessoSeletivoDiscente. "*/
	public static final String ANO_INICIO_UTILIZACAO_CONVOCACAO_DISCENTE = "2_10500_49";
	
	/** N�mero m�ximo de orienta��es acad�micas dos doscentes"*/
	public static final String NUMERO_MAXIMO_ORIENTACOES = "2_10500_50";
	
	/**
	 * Indica �ndice Acad�mico que era utilizado anteriormente na elabora��o do relat�rios de alunos Laureados, o ira.
	 */
	public static final String INDICE_ACADEMICO_LAUREADOS_ANTIGO_IRA = "2_10500_51";
	
	/**
	 * Indica o valor m�nimo do �ndice Acad�mico que era utilizado anteriormente, o ira, a ser considerado na elabora��o do relat�rio de alunos laureados. 
	 */
	public static final String VALOR_MINIMO_INDICE_ACADEMICO_LAUREADOS_ANTIGO_IRA = "2_10500_52";
	
	/**
	 * Este par�metro diz se alunos EAD fazem matr�cula on-line. Caso negativo quem faz a matr�cula do aluno � o tutor. 
	 */
	public static final String ALUNO_EAD_FAZ_MATRICULA_ONLINE = "2_10500_53";

	/** Ano inicial do cadastro da carga hor�ria dedicada pelo docente ao ensino � dist�ncia.*/
	public static final String ANO_INICIO_CADASTRO_CH_DEDICADA_EAD  = "2_10500_54";
	
	/** Nome da Pr�-Reitoria de Gradua��o. */
	public static final String NOME_PRO_REITORIA_GRADUACAO = "2_10500_55";
		
	/** Telefone e e-mail da Nome da Pr�-Reitoria de Gradua��o. */
	public static final String CONTATO_PRO_REITORIA_GRADUACAO = "2_10500_56";
	
	/**
	 * Indica o tipo de prioridade da forma de sele��o dos alunos na transfer�ncia de turma, 
	 * podendo este ser selecionado por conforma o n�mero de matr�cula ou de forma aleat�ria.
	 * EX: RAND, PRIO 
	 */
	public static final String TIPO_ESTRATEGIA_TRANSF_TURMA = "2_10500_57";
	
	/**
	 * Indica se � permitido cadastrar a orienta��o da atividade com docente externo.
	 */
	public static final String PERMITE_DOCENTE_EXTERNO_ORIENTAR_ATIVIDADE = "2_10500_58";
	
	/**
	 * Indica se � permitido o coordenador do curso realizar a altera��o por completo dos dados pessoais do aluno.
	 */
	public static final String PERMITE_ALTERACAO_COMPLETA_DADOS_PESSOAIS = "2_10500_59";
	
	/** Ano inicial para o cadastro dos cursos avaliados pelo ENADE INGRESSANTE. */
	public static final String ANO_INICIO_ENADE_INGRESSANTE = "2_10500_61";

	/** Ano inicial para o cadastro dos cursos avaliados pelo ENADE CONCLUINTE. */
	public static final String ANO_INICIO_ENADE_CONCLUINTE = "2_10500_62";
	
	/** N�mero m�nimo de alunos conclu�dos que um curso deve ter para que o c�lculo do MCN utilize a m�dia do MCN do curso, e n�o a do centro. */
	public static final String NUMERO_MINIMO_CONCLUIDOS_CALCULO_MCN = "2_10500_63";

	/** Define se as turmas EAD podem possuir horario. Caso TRUE ent�o o formul�rio de defini��o de 
	 * hor�rios aparecer� quando for turmas EAD. Por�m as valida��es de hor�rios n�o ser�o executadas. 
	 * Caso FALSE o formul�rio de defini��o de hor�rio n�o aparecer� na cria��o das turmas e elas ser�o registradas sem hor�rio.*/
	public static final String  DEFINE_HORARIO_TURMA_EAD = "2_10500_64";
	
	/** 
	 * Define se o coordenador do curso pode solicitar turma de f�rias sem que os discentes tenham realizado a solicita��o.
	 * Caso FALSE o coordenador s� poder� solicitar turmas se algum discente tiver solicitado.
	 */
	public static final String DEFINE_SOLICITACAO_TURMA_FERIAS_SEM_SOLICITACAO = "2_10500_65";
	
	
	/**
	 * Nome do Departamento de Administra��o Escolar  
	 */
	public static final String NOME_ADM_ESCOLAR = "2_10500_66";
	
	/**
	 * Nome do setor respons�vel pela gest�o de Gradua��o
	 */
	public static final String NOME_GESTAO_GRADUACAO = "2_10500_67";
	
	
	/**
	 * Sigla do departamento de administra��o escolar
	 */
	public static final String SIGLA_DEPARTAMENTO_ADM_ESCOLAR = "2_10500_68";

	/** Quantidade m�xima de discentes em uma turma para que o chefe possa exclu�-la no per�odo de ajustes. */
	public static final String QTD_MAX_DISCENTES_PERMITE_REMOCAO_TURMA_PELO_CHEFE = "2_10500_69";
	
	/**
	 * Indica a quantidade m�xima de dias para o chefe homologar uma reposi��o de prova. 
	 */
	public static final String QUANTIDADE_MAXIMA_DIAS_HOMOLOGACAO_REPOSICAO_PROVA = "2_10500_70";	
	
	/**
	 * Porcentagem m�nima que o n�mero de aula precisa ter em rela��o a cargar hor�ria do componente curricular. num_aulas >= X% da ch 
	 */
	public static final String PORCENTAGEM_MIN_NUM_AULAS_EM_RELACAO_CH_TURMA = "2_10500_71";	
	
	/**
	 * Porcentagem m�xima que o n�mero de aula precisa ter em rela��o a cargar hor�ria do componente curricular. num_aulas <= X% da ch
	 */
	public static final String PORCENTAGEM_MAX_NUM_AULAS_EM_RELACAO_CH_TURMA = "2_10500_72";
	
	/**
	 * Define se alunos especiais de gradua��o podem fazer matr�cula online
	 */
	public static final String ALUNO_ESPECIAL_FAZ_MATRICULA_ONLINE = "2_10500_74";
	
	/**
	 * C�digo utilizado para as turmas de ensino individualizado. 
	 */
	public static final String CODIGO_TURMA_ENSINO_INDIVIDUALIZADO = "2_10500_75";

	/** Texto com o trecho do regulamento da gradua��o que trata da valida��o de atividades,
	 * a ser exibido para o usu�rio no formul�rio de valida��o.
	 */
	public static final String TEXTO_RESOLUCAO_VALIDACAO_ATIVIDADE_GRADUACAO = "2_10500_76";	
}