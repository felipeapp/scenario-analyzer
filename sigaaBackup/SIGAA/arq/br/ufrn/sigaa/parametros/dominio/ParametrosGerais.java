/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em 20/05/2009
 */
package br.ufrn.sigaa.parametros.dominio;

import br.ufrn.sigaa.ensino.timer.CalculosDiscenteGraduacaoTimer;

/**
 * Interface contendo todos os par�metros gerais do SIGAA,
 * que n�o pertencem a um m�dulo espec�fico.
 * @author David Pereira
 *
 */
public interface ParametrosGerais {


	/**
	 * URL de acesso ao sistema
	 */
	public static final String ENDERECO_ACESSO = "2_10100_1";

	/**
	 * Caminho no sistema de arquivos onde os relat�rios do resultado do processamento de matr�cula est�o armazenados
	 */
	public static final String CAMINHO_RESULTADO_PROCESSAMENTO = "2_10100_2";

	/**
	 * Caminho completo no sistema de arquivos onde os relat�rios do resultado do processamento de matr�cula est�o armazenados
	 */
	public static final String CAMINHO_COMPLETO_RESULTADO_PROCESSAMENTO = "2_10100_3";

	/**
	 * Capacidade m�xima do porta arquivos (em bytes), por usu�rio
	 */
	public static final String TAMANHO_MAXIMO_PORTA_ARQUIVOS = "2_10100_7";

	/**
	 * URL para realizar autentica��o de documentos emitidos pelo SIGAA
	 */
	public static final String ENDERECO_AUTENTICIDADE = "2_10100_8";

	/**
	 * Senha especial para a realiza��o do processamento de matr�culas
	 */
	public static final String SENHA_PROCESSAMENTO_MATRICULA = "2_10100_9";


	/**
	 * Per�odo adicional (em dias), ap�s o fim de seu mandato,
	 * no qual o docente externo mant�m seu acesso aos sistema
	 */
	public static final String PERIODO_ACESSO_ADICIONAL_DOCENTE_EXTERNO = "2_10100_10";

	/**
	 * Porcentagem m�nima de aulas para as quais o professor deve preencher t�picos
	 * de aula para o di�rio de classe.
	 */
	public static final String PORCENTAGEM_MINIMA_AULAS_DIARIO = "2_10100_11";

	/**
	 * Porcentagem m�nima de frequ�ncias que o professor deve lan�ar
	 * no di�rio de classe.
	 */
	public static final String PORCENTAGEM_MINIMA_FREQUENCIA_DIARIO = "2_10100_12";

	/**
	 * N�veis de ensino para os quais � obrigat�rio o preenchimento eletr�nico do
	 * di�rio de classe.
	 */
	public static final String NIVEIS_ENSINO_OBRIGATORIEDADE_DIARIO = "2_10100_13";

	/**
	 * Quantidade de livros a exibir na lista de "�ltimos livros registrados".
	 */
	public static final String QUANTIDADE_ULTIMOS_LIVROS_REGISTRADOS = "2_10100_14";

	/**
	 * Quantidade de solicita��es de trancamentos a processar pela thread de trancamento.
	 */
	public static final String QUANTIDADE_SOLICITACOES_TRANCAMENTO_PROCESSAR = "2_10100_15";

	/**
	 * Quantidade de discentes a calcular no {@link CalculosDiscenteGraduacaoTimer}
	 */
	public static final String QUANTIDADE_CALCULAR_CALCULOS_DISCENTE_GRADUACAO_TIMER = "2_10100_16";

	/** Ano inicial de cadastro de turmas. */
	public static final String ANO_INICIAL_CADASTRO_TURMAS = "2_10100_17";
	
	/**
	 * Indica se os c�digos dos componentes curriculares ser�o validados em rela��o � forma literal.
	 */
	public static final String VALIDAR_CODIGO_COMPONENTE_CURRICULAR_ENSINO = "2_10100_18";	

	/**
	 * Define se o pagamento de cursos de lato sensu da institui��o ser� realizado atrav�s do sistema, pelos projetos de cursos e concursos.
	 */
	public static final String PAGAR_CURSO_LATO_SENSU_ATRAVES_DE_CURSOS_E_CONCURSOS = "7_101200_10";	
	
	/** Define se os memorandos eletr�nicos est�o ativos no SIGAA */
	public static final String MEMORANDOS_ATIVOS_SIGAA = "2_0_3";
	
	/** Valor determinante para o uso de GRU Simples ou GRU Cobran�a. A partir do valor definido neste par�metro, ser� usada a GRU Cobran�a para o recolhimento. Caso contr�rio, GRU Simples. */
	public static final String VALOR_MAXIMO_GRU_SIMPLES = "2_10100_24";
	
	/** N�mero m�ximo de letras poss�veis no c�digo do componente. */
	public static final String QTD_LETRAS_CODIGO_COMPONENTE = "2_10100_25";
	
	/** Quantidade de caracteres que o c�digo deve ter. */
	public static final String TAMANHO_CODIGO_COMPONENTE = "2_10100_26";
	
	/** Valor da carga hor�ria referente a dedica��o exclusiva do docente. */
	public static final String CARGA_HORARIA_DEDICACAO_EXCLUSIVA = "2_10100_27";
	
	/** Par�metro que define que o portal discente dever� ser exibido em seu modo b�sico, para fazer menos consultas em �pocas de maior demanda */
	public static final String PORTAL_DISCENTE_MODO_REDUZIDO = "2_12100_1"; 
	
	/** Valor do identificador da UFRN na base de dados de Institui��es de Ensino. */
	public static final String IDENTIFICADOR_INSTITUICAO_UFRN = "2_10100_28";
	
	/** Valor do c�digo da UFRN no sistema da CAPES. */
	public static final String COD_CAPES_UFRN = "2_10100_29";
	
	/** Email utilizado pela Comiss�o de Apoio a Estudantes com Necessidades Especiais (CAENE).  */
	public static final String EMAIL_CAENE = "2_10100_30";

	/** Constante que determina a quantidade de d�gitos na sequ�ncia num�rica da matr�cula. */
	public static final String QUANTIDADE_DIGITOS_SEQUENCIA_MATRICULA = "2_10100_31"; 
	
	/** Classe que determina como ser� a composi��o da matr�cula atribu�da � um discente. */
	public static final String ESTRATEGIA_COMPOSICAO_MATRICULA = "2_10100_32";
	
	/** Constante que determina a pasta ra�z para os arquivos que s�o gerados no chat para armazenar as mensagens. */
	public static final String PASTA_RAIZ_CHAT = "2_10100_33";
	
	/** Nome do Pr�-Reitor de gradua��o. Utilizado nos certificados. */
	public static final String NOME_PRO_REITOR_RESCURSOS_HUMANOS = "2_10100_34";
	
	/** Nome da classe que implementa a interface que define o factory da estrat�gia de consolida��o. */
	public static final String IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY = "2_10100_35";
	
	/** URL que indica onde efetuar o download do software para convers�o de arquivos textos para o formato PDF. */
	public static final String ENDERECO_EXPORTAR_FORMATO_PDF = "2_10100_36";
	
	/** Situa��es de servidor permitidas para fazer login */
	public static final String VINCULOS_SITUACAO_SERVIDOR_PERMITIDO = "2_10100_37";
	
	/** Indica se a inscri��o para o projeto Mes�rio Universit�rio est� ativa ou n�o.*/ 
	public static final String MESARIO_VOLUNTARIO_ATIVO = "2_10100_39";

	/** N�mero de dias ap�s o fim da inscri��o que o processo ainda deve ser exibido. */
	public static final String NUMERO_DIAS_PASSADOS_PUBLICACAO_PROCESSO_SELETIVO = "2_10100_40";
	
	/** Indica se na consolida��o de atividade especial deve-se validar a quantidade das frequ�ncias e t�picos de aula lan�ados para a turma. */
	public static final String ATIVIDADE_ESPECIAL_DEVE_VALIDAR_FREQUENCIAS_E_TOPICOS = "2_10100_41";
	
	/** Identificador no banco do registro referente � unidade federativa do DISTRITO FEDERAL. */
	public static final String ID_UF_DISTRITO_FEDERAL = "2_10100_42";
	
	/** Classe que trata da inser��o de documentos digitalizados do discente no sistema. */ 
	public static final String GERENCIADOR_ELETRONICO_DOCUMENTACAO_DISCENTE = "2_10100_56";

	/** Identificadores no banco dos cargos de docentes que possuem acesso ao preenchimento do PID. */
	public static final String IDS_CARGOS_ACESSO_PID = "2_10100_59";
}