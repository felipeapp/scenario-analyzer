/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '19/05/2009'
 *
 */
package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os parâmetros do módulo
 * de graduação.
 * @author David Pereira
 * @author wendell
 *
 */
public interface ParametrosGraduacao {

	/**
	 * Data de início da revalidação de diplomas de graduação
	 */
	public static final String INICIO_PERIODO_REVALIDACAO = "2_10500_1";
	
	/**
	 * Data de fim da revalidação de diplomas de graduação
	 */
	public static final String FIM_PERIODO_REVALIDACAO = "2_10500_2";
	
	/**
	 * Ano a ser considerado para validações de carga horária complementar de estruturas
	 * curriculares, de acordo com a resolução do CONSEPE onde esta validação passou a ser exigida
	 */
	public static final String ANO_RESOLUCAO_CONSEPE = "2_10500_3";
	
	/**
	 * Lista de e-mails das pessoas que devem ser notificadas com o resumo das faltas
	 * de docentes informadas pelos alunos
	 */
	public static final String EMAIL_RELATORIO_FALTA_DOCENTE = "2_10500_4";
	
	/** idPessoa do Diretor do DAE, utilizado no registro de diplomas. */
	public static final String ID_PESSOA_DIRETOR_DAE = "2_10500_5";

	/** idPessoa do Diretor da Divisão de Registro de Diplomas */
	public static final String ID_PESSOA_DIRETOR_DRED = "2_10500_6";
	
	/** Ano de início do registro de diplomas no SIGAA. */
	public static final String ANO_INICIO_REGISTRO_DIPLOMA = "2_10500_7";
	
	/** Semestre de início do registro de diplomas no SIGAA. */
	public static final String SEMESTRE_INICIO_REGISTRO_DIPLOMA = "2_10500_8";
	
	/**
	 * Lista, separada por vírgula, de códigos de unidade que deverão ignorar a sigla utilizada no
	 * cadastro de componentes curriculares. Exemplo, para o Departamento de
	 * Artes poder cadastrar disciplina com o a sigla DGN, infomar o código 1313.
	 */
	public static final String CODIGO_UNIDADES_NAO_VERIFICAR_SIGLA_NO_CADASTRO_DE_COMPONENTES = "2_10500_9";

	/**
	 * Define a CH máxima de um componente que pode ser oferecida no período letivo de férias
	 */
	public static final String CARGA_HORARIA_DISCIPLINA_FERIAS = "2_10500_10";
	
	/**
	 * Define a permissão de acesso das coordenações de cursos de graduação para a operação de matrícula em turmas de férias. 
	 */
	public static final String ACESSO_COORDENACAO_MATRICULA_TURMA_DE_FERIAS = "2_10500_11";
	
	/**
	 * Define o início da sequência de Registro de Diplomas no SIGAA para a graduação.  
	 */
	public static final String NUMERO_INICIAL_REGISTRO_DIPLOMA = "2_10500_12";
	
	/**
	 * Porcentagem mínima que define como calcular o perfil inicial do discente.
	 */
	public static final String PERFIL_INICIAL_PORCENTAGEM_MINIMA_CH = "2_10500_13";
	
	/**
	 * Define se no cálculo do prazo máximo, o perfil inicial vai ser considerado.
	 * 
	 * Para alunos a quem seja atribuído um perfil inicial diferente de 0 (zero), 
	 * o número de níveis adicionais será descontado do número de 
	 * períodos máximo para conclusão do curso.
	 */
	public static final String CALCULO_PRAZO_FINAL_DEVE_CONSIDERAR_PERFIL_INICIAL = "2_10500_14";
	
	/**
	 * Define a quantidade máxima de horas por turno que um discente pode ter.
	 */
	public static final String CARGA_HORARIA_AULA_MAXIMA_POR_TURNO = "2_10500_15";
	
	/**
	 * Define a quantidade máxima de horas por dia que um discente pode ter.
	 */
	public static final String CARGA_HORARIA_AULA_MAXIMA_POR_DIA = "2_10500_16";
	
	/**
	 * Indica o tempo médio necessário para realizar os cálculos de um discente
	 * de graduação.
	 */
	public static final String TEMPO_MEDIO_CALCULOS_POR_DISCENTE = "2_10500_17";
	
	/**
	 * Indica o Id do curso de Comunicação Social.
	 */
	public static final String ID_CURSO_COMUNICACAO_SOCIAL = "2_10500_18";
	
	/**
	 * Indica o Id do curso de Psicologia.
	 */
	public static final String ID_CURSO_PSICOLOGIA = "2_10500_19";	
	
	/**
	 * Indica o Id da Área de Conhecimento CNPJ de Ciências da Saúde.
	 */
	public static final String ID_AREA_CCS = "2_10500_20";		
	
	/**
	 * Indica a quantidade máximas de mudanças de turno na graduação.
	 */
	public static final String QTD_MAXIMA_MUDANCA_TURNO = "2_10500_21";

	/**
	 * Indica Índice Acadêmico utilizado na elaboração do relatórios de alunos Laureados.
	 */
	public static final String INDICE_ACADEMICO_LAUREADOS = "2_10500_24";
	
	/**
	 * Indica o valor mínimo do Índice Acadêmico, a ser considerado na elaboração do relatório de alunos laureados. 
	 */
	public static final String VALOR_MINIMO_INDICE_ACADEMICO_LAUREADOS = "2_10500_25";
	
	/**
	 * Indica a quantidade de dias para frente em relação ao dia corrente,
	 * considerado na amostragem das atividades (avaliações e tarefas)
	 * na página principal do Portal do Discente.
	 */
	public static final String QTD_DIAS_FRENTE_ATIVIDADES = "2_10500_27";
	
	/**
	 * Indica a quantidade de dias para trás em relação ao dia corrente,
	 * considerado na amostragem das atividades (avaliações e tarefas)
	 * na página principal do Portal do Discente. 
	 */
	public static final String QTD_DIAS_TRAS_ATIVIDADES = "2_10500_28";		
	
	/**
	 * Indica o mês e ano limite utilizado para definir se um aluno é antigo e inativo,
	 * para realizar o controle de qual cálculo rendimento escolar será exibido no histórico do aluno.
	 * Se a data de saída deste aluno for menor ou igual a esta data parametrizada será exibido o IRA, 
	 * ao invés dos índices acadêmicos.
	 */
	public static final String MES_ANO_LIMITE_ALUNOS_ANTIGOS_INATIVOS = "2_10500_29";		
	
	/**
	 * Indica a quantidade mínima de dias da realização da prova de reposição após confirmação. 
	 */
	public static final String QUANTIDADE_MIN_DIAS_REALIZACAO_REPOSICAO_PROVA = "2_10500_30";
	
	/**
	 * Indica a quantidade máxima de dias para solicitar a reposição de prova. 
	 */
	public static final String QUANTIDADE_MAXIMA_DIAS_REPOSICAO_PROVA = "2_10500_31";	
	
	/**
	 * Indica a quantidade máxima de discentes em uma turma de ensino individual. 
	 */
	public static final String QTD_MAX_DISCENTES_TURMA_ENSINO_INDIVIDUAL = "2_10500_32";
	
	/**
	 * Indica o Id do curso de Administração à Distância.
	 */
	public static final String ID_CURSO_ADMINISTRACAO_A_DISTANCIA = "2_10500_33";
	
	/**
	 * Indica se estará habilitado ou não o procedimento de concordância com o regulamento dos cursos de graduação
	 * realizado pelos alunos na operação de matrícula on-line.
	 */
	public static final String CONCORDANCIA_REGULAMENTO = "2_10500_35";
	
	/**
	 * Indica a quantidade mínima de discentes em uma turma de férias 
	 */
	public static final String QTD_MIN_DISCENTES_TURMA_FERIAS = "2_10500_36";
	
	/**
	 * Indica se estará habilitado ou não o procedimento de atualização dos dados pessoais pelos discentes durante a matrícula on-line.
	 */
	public static final String ATUALIZAR_DADOS_PESSOAIS_MATRICULA_ONLINE = "2_10500_37";

	/**
	 * Indica o ano e periodo dos discentes que serão incluidos no novo cálculo do prazo máximo
	 */
	public static final String ANO_PERIODO_PERFIL_INICIAL_DIMINUINDO_PRAZO_MAXIMO = "2_10500_38";
	
	
	/**
	 * Sigla - Nome do Departamento de Administração Escolar
	 */
	public static final String SIGLA_NOME_ADM_ESCOLAR = "2_10500_39";
	
	/**
	 * Sigla - Nome do setor responsável pela gestão de Graduação
	 */
	public static final String SIGLA_NOME_GESTAO_GRADUACAO = "2_10500_40";
	
	/**
	 * Verifica se a Instituição utilizará o prazo para limite de trancamento de turmas no módulo graduação,
	 *  para as solicitações de trancamento, executadas durante o timer de trancamento.
	 */
	public static final String UTILIZAR_PRAZO_TRANCAMENTO_TURMA = "2_10500_42";
	
	/**
	 * Indica o valor do identificador do índice acadêmico MC(Média de Conclusão).
	 */
	public static final String ID_MC = "2_10500_43";
	
	/**
	 * Indica o valor do identificador do índice acadêmico MCN(Média de Conclusão Normalizada).
	 */
	public static final String ID_MCN = "2_10500_44";
	
	
	/**
	 * Indica se vai ser permitido a alteração da Grade de Horários para o nível de ensino.
	 */
	public static final String PERMITE_ESCOLHA_GRADE_HORARIOS = "2_10500_45";
	
	/** Taxa de revalidação de diploma */
	public static final String TAXA_REVALIDACAO_DIPLOMA = "2_10500_47";
	
	/** Descrição utilizada para a convocação da primeira chamada do vestibular. "1ª Chamada "*/
	public static final String DESCRICAO_PRIMEIRA_CHAMADA_VESTIBULAR = "2_10500_48";
	
	/** Ano de início da utilização do objeto ConvocacaoProcessoSeletivoDiscente. "*/
	public static final String ANO_INICIO_UTILIZACAO_CONVOCACAO_DISCENTE = "2_10500_49";
	
	/** Número máximo de orientações acadêmicas dos doscentes"*/
	public static final String NUMERO_MAXIMO_ORIENTACOES = "2_10500_50";
	
	/**
	 * Indica Índice Acadêmico que era utilizado anteriormente na elaboração do relatórios de alunos Laureados, o ira.
	 */
	public static final String INDICE_ACADEMICO_LAUREADOS_ANTIGO_IRA = "2_10500_51";
	
	/**
	 * Indica o valor mínimo do Índice Acadêmico que era utilizado anteriormente, o ira, a ser considerado na elaboração do relatório de alunos laureados. 
	 */
	public static final String VALOR_MINIMO_INDICE_ACADEMICO_LAUREADOS_ANTIGO_IRA = "2_10500_52";
	
	/**
	 * Este parâmetro diz se alunos EAD fazem matrícula on-line. Caso negativo quem faz a matrícula do aluno é o tutor. 
	 */
	public static final String ALUNO_EAD_FAZ_MATRICULA_ONLINE = "2_10500_53";

	/** Ano inicial do cadastro da carga horária dedicada pelo docente ao ensino à distância.*/
	public static final String ANO_INICIO_CADASTRO_CH_DEDICADA_EAD  = "2_10500_54";
	
	/** Nome da Pró-Reitoria de Graduação. */
	public static final String NOME_PRO_REITORIA_GRADUACAO = "2_10500_55";
		
	/** Telefone e e-mail da Nome da Pró-Reitoria de Graduação. */
	public static final String CONTATO_PRO_REITORIA_GRADUACAO = "2_10500_56";
	
	/**
	 * Indica o tipo de prioridade da forma de seleção dos alunos na transferência de turma, 
	 * podendo este ser selecionado por conforma o número de matrícula ou de forma aleatória.
	 * EX: RAND, PRIO 
	 */
	public static final String TIPO_ESTRATEGIA_TRANSF_TURMA = "2_10500_57";
	
	/**
	 * Indica se é permitido cadastrar a orientação da atividade com docente externo.
	 */
	public static final String PERMITE_DOCENTE_EXTERNO_ORIENTAR_ATIVIDADE = "2_10500_58";
	
	/**
	 * Indica se é permitido o coordenador do curso realizar a alteração por completo dos dados pessoais do aluno.
	 */
	public static final String PERMITE_ALTERACAO_COMPLETA_DADOS_PESSOAIS = "2_10500_59";
	
	/** Ano inicial para o cadastro dos cursos avaliados pelo ENADE INGRESSANTE. */
	public static final String ANO_INICIO_ENADE_INGRESSANTE = "2_10500_61";

	/** Ano inicial para o cadastro dos cursos avaliados pelo ENADE CONCLUINTE. */
	public static final String ANO_INICIO_ENADE_CONCLUINTE = "2_10500_62";
	
	/** Número mínimo de alunos concluídos que um curso deve ter para que o cálculo do MCN utilize a média do MCN do curso, e não a do centro. */
	public static final String NUMERO_MINIMO_CONCLUIDOS_CALCULO_MCN = "2_10500_63";

	/** Define se as turmas EAD podem possuir horario. Caso TRUE então o formulário de definição de 
	 * horários aparecerá quando for turmas EAD. Porém as validações de horários não serão executadas. 
	 * Caso FALSE o formulário de definição de horário não aparecerá na criação das turmas e elas serão registradas sem horário.*/
	public static final String  DEFINE_HORARIO_TURMA_EAD = "2_10500_64";
	
	/** 
	 * Define se o coordenador do curso pode solicitar turma de férias sem que os discentes tenham realizado a solicitação.
	 * Caso FALSE o coordenador só poderá solicitar turmas se algum discente tiver solicitado.
	 */
	public static final String DEFINE_SOLICITACAO_TURMA_FERIAS_SEM_SOLICITACAO = "2_10500_65";
	
	
	/**
	 * Nome do Departamento de Administração Escolar  
	 */
	public static final String NOME_ADM_ESCOLAR = "2_10500_66";
	
	/**
	 * Nome do setor responsável pela gestão de Graduação
	 */
	public static final String NOME_GESTAO_GRADUACAO = "2_10500_67";
	
	
	/**
	 * Sigla do departamento de administração escolar
	 */
	public static final String SIGLA_DEPARTAMENTO_ADM_ESCOLAR = "2_10500_68";

	/** Quantidade máxima de discentes em uma turma para que o chefe possa excluí-la no período de ajustes. */
	public static final String QTD_MAX_DISCENTES_PERMITE_REMOCAO_TURMA_PELO_CHEFE = "2_10500_69";
	
	/**
	 * Indica a quantidade máxima de dias para o chefe homologar uma reposição de prova. 
	 */
	public static final String QUANTIDADE_MAXIMA_DIAS_HOMOLOGACAO_REPOSICAO_PROVA = "2_10500_70";	
	
	/**
	 * Porcentagem mínima que o número de aula precisa ter em relação a cargar horária do componente curricular. num_aulas >= X% da ch 
	 */
	public static final String PORCENTAGEM_MIN_NUM_AULAS_EM_RELACAO_CH_TURMA = "2_10500_71";	
	
	/**
	 * Porcentagem máxima que o número de aula precisa ter em relação a cargar horária do componente curricular. num_aulas <= X% da ch
	 */
	public static final String PORCENTAGEM_MAX_NUM_AULAS_EM_RELACAO_CH_TURMA = "2_10500_72";
	
	/**
	 * Define se alunos especiais de graduação podem fazer matrícula online
	 */
	public static final String ALUNO_ESPECIAL_FAZ_MATRICULA_ONLINE = "2_10500_74";
	
	/**
	 * Código utilizado para as turmas de ensino individualizado. 
	 */
	public static final String CODIGO_TURMA_ENSINO_INDIVIDUALIZADO = "2_10500_75";

	/** Texto com o trecho do regulamento da graduação que trata da validação de atividades,
	 * a ser exibido para o usuário no formulário de validação.
	 */
	public static final String TEXTO_RESOLUCAO_VALIDACAO_ATIVIDADE_GRADUACAO = "2_10500_76";	
}