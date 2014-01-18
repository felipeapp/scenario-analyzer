/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 20/05/2009
 */
package br.ufrn.sigaa.parametros.dominio;

import br.ufrn.sigaa.ensino.timer.CalculosDiscenteGraduacaoTimer;

/**
 * Interface contendo todos os parâmetros gerais do SIGAA,
 * que não pertencem a um módulo específico.
 * @author David Pereira
 *
 */
public interface ParametrosGerais {


	/**
	 * URL de acesso ao sistema
	 */
	public static final String ENDERECO_ACESSO = "2_10100_1";

	/**
	 * Caminho no sistema de arquivos onde os relatórios do resultado do processamento de matrícula estão armazenados
	 */
	public static final String CAMINHO_RESULTADO_PROCESSAMENTO = "2_10100_2";

	/**
	 * Caminho completo no sistema de arquivos onde os relatórios do resultado do processamento de matrícula estão armazenados
	 */
	public static final String CAMINHO_COMPLETO_RESULTADO_PROCESSAMENTO = "2_10100_3";

	/**
	 * Capacidade máxima do porta arquivos (em bytes), por usuário
	 */
	public static final String TAMANHO_MAXIMO_PORTA_ARQUIVOS = "2_10100_7";

	/**
	 * URL para realizar autenticação de documentos emitidos pelo SIGAA
	 */
	public static final String ENDERECO_AUTENTICIDADE = "2_10100_8";

	/**
	 * Senha especial para a realização do processamento de matrículas
	 */
	public static final String SENHA_PROCESSAMENTO_MATRICULA = "2_10100_9";


	/**
	 * Período adicional (em dias), após o fim de seu mandato,
	 * no qual o docente externo mantém seu acesso aos sistema
	 */
	public static final String PERIODO_ACESSO_ADICIONAL_DOCENTE_EXTERNO = "2_10100_10";

	/**
	 * Porcentagem mínima de aulas para as quais o professor deve preencher tópicos
	 * de aula para o diário de classe.
	 */
	public static final String PORCENTAGEM_MINIMA_AULAS_DIARIO = "2_10100_11";

	/**
	 * Porcentagem mínima de frequências que o professor deve lançar
	 * no diário de classe.
	 */
	public static final String PORCENTAGEM_MINIMA_FREQUENCIA_DIARIO = "2_10100_12";

	/**
	 * Níveis de ensino para os quais é obrigatório o preenchimento eletrônico do
	 * diário de classe.
	 */
	public static final String NIVEIS_ENSINO_OBRIGATORIEDADE_DIARIO = "2_10100_13";

	/**
	 * Quantidade de livros a exibir na lista de "últimos livros registrados".
	 */
	public static final String QUANTIDADE_ULTIMOS_LIVROS_REGISTRADOS = "2_10100_14";

	/**
	 * Quantidade de solicitações de trancamentos a processar pela thread de trancamento.
	 */
	public static final String QUANTIDADE_SOLICITACOES_TRANCAMENTO_PROCESSAR = "2_10100_15";

	/**
	 * Quantidade de discentes a calcular no {@link CalculosDiscenteGraduacaoTimer}
	 */
	public static final String QUANTIDADE_CALCULAR_CALCULOS_DISCENTE_GRADUACAO_TIMER = "2_10100_16";

	/** Ano inicial de cadastro de turmas. */
	public static final String ANO_INICIAL_CADASTRO_TURMAS = "2_10100_17";
	
	/**
	 * Indica se os códigos dos componentes curriculares serão validados em relação à forma literal.
	 */
	public static final String VALIDAR_CODIGO_COMPONENTE_CURRICULAR_ENSINO = "2_10100_18";	

	/**
	 * Define se o pagamento de cursos de lato sensu da instituição será realizado através do sistema, pelos projetos de cursos e concursos.
	 */
	public static final String PAGAR_CURSO_LATO_SENSU_ATRAVES_DE_CURSOS_E_CONCURSOS = "7_101200_10";	
	
	/** Define se os memorandos eletrônicos estão ativos no SIGAA */
	public static final String MEMORANDOS_ATIVOS_SIGAA = "2_0_3";
	
	/** Valor determinante para o uso de GRU Simples ou GRU Cobrança. A partir do valor definido neste parâmetro, será usada a GRU Cobrança para o recolhimento. Caso contrário, GRU Simples. */
	public static final String VALOR_MAXIMO_GRU_SIMPLES = "2_10100_24";
	
	/** Número máximo de letras possíveis no código do componente. */
	public static final String QTD_LETRAS_CODIGO_COMPONENTE = "2_10100_25";
	
	/** Quantidade de caracteres que o código deve ter. */
	public static final String TAMANHO_CODIGO_COMPONENTE = "2_10100_26";
	
	/** Valor da carga horária referente a dedicação exclusiva do docente. */
	public static final String CARGA_HORARIA_DEDICACAO_EXCLUSIVA = "2_10100_27";
	
	/** Parâmetro que define que o portal discente deverá ser exibido em seu modo básico, para fazer menos consultas em épocas de maior demanda */
	public static final String PORTAL_DISCENTE_MODO_REDUZIDO = "2_12100_1"; 
	
	/** Valor do identificador da UFRN na base de dados de Instituições de Ensino. */
	public static final String IDENTIFICADOR_INSTITUICAO_UFRN = "2_10100_28";
	
	/** Valor do código da UFRN no sistema da CAPES. */
	public static final String COD_CAPES_UFRN = "2_10100_29";
	
	/** Email utilizado pela Comissão de Apoio a Estudantes com Necessidades Especiais (CAENE).  */
	public static final String EMAIL_CAENE = "2_10100_30";

	/** Constante que determina a quantidade de dígitos na sequência numérica da matrícula. */
	public static final String QUANTIDADE_DIGITOS_SEQUENCIA_MATRICULA = "2_10100_31"; 
	
	/** Classe que determina como será a composição da matrícula atribuída à um discente. */
	public static final String ESTRATEGIA_COMPOSICAO_MATRICULA = "2_10100_32";
	
	/** Constante que determina a pasta raíz para os arquivos que são gerados no chat para armazenar as mensagens. */
	public static final String PASTA_RAIZ_CHAT = "2_10100_33";
	
	/** Nome do Pró-Reitor de graduação. Utilizado nos certificados. */
	public static final String NOME_PRO_REITOR_RESCURSOS_HUMANOS = "2_10100_34";
	
	/** Nome da classe que implementa a interface que define o factory da estratégia de consolidação. */
	public static final String IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY = "2_10100_35";
	
	/** URL que indica onde efetuar o download do software para conversão de arquivos textos para o formato PDF. */
	public static final String ENDERECO_EXPORTAR_FORMATO_PDF = "2_10100_36";
	
	/** Situações de servidor permitidas para fazer login */
	public static final String VINCULOS_SITUACAO_SERVIDOR_PERMITIDO = "2_10100_37";
	
	/** Indica se a inscrição para o projeto Mesário Universitário está ativa ou não.*/ 
	public static final String MESARIO_VOLUNTARIO_ATIVO = "2_10100_39";

	/** Número de dias após o fim da inscrição que o processo ainda deve ser exibido. */
	public static final String NUMERO_DIAS_PASSADOS_PUBLICACAO_PROCESSO_SELETIVO = "2_10100_40";
	
	/** Indica se na consolidação de atividade especial deve-se validar a quantidade das frequências e tópicos de aula lançados para a turma. */
	public static final String ATIVIDADE_ESPECIAL_DEVE_VALIDAR_FREQUENCIAS_E_TOPICOS = "2_10100_41";
	
	/** Identificador no banco do registro referente à unidade federativa do DISTRITO FEDERAL. */
	public static final String ID_UF_DISTRITO_FEDERAL = "2_10100_42";
	
	/** Classe que trata da inserção de documentos digitalizados do discente no sistema. */ 
	public static final String GERENCIADOR_ELETRONICO_DOCUMENTACAO_DISCENTE = "2_10100_56";

	/** Identificadores no banco dos cargos de docentes que possuem acesso ao preenchimento do PID. */
	public static final String IDS_CARGOS_ACESSO_PID = "2_10100_59";
}