/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Data de Criação: 24/08/2010
 */
package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os parâmetros do módulo técnico
 * 
 * @author Igor Linnik.
 *
 */
public interface ParametrosTecnico {

	/**
	 * Indica se vai ser permitido a alteração da Grade de Horários para o nível de ensino.
	 */
	public static final String PERMITE_ESCOLHA_GRADE_HORARIOS = "2_10400_1";
	
	/**
	 * Parâmetro multivalorado contendo os identificadores dos cursos do Metrópole Digital.
	 */
	public static final String ID_CURSO_METROPOLE_DIGITAL = "2_10400_4";
	
	/**
	 * Parâmetro multivalorado contendo os identificadores das ênfases do módulo avançado do Metrópole Digital.
	 */
	public static final String ID_ENFASES_MODULO_AVANCADO_METROPOLE_DIGITAL = "2_10400_5";
	
	/**
	 * Ano de ingresso dos alunos que podem efetuar matrícula no módulo avançado.
	 */
	public static final String ANO_INGRESSO_PERMITE_MATRICULA_MODULO_AVANCADO = "2_10400_6";
	
	/** Assunto do email enviado aos candidatos do processo seletivo que foram convocados. */
	public static final String ASSUNTO_EMAIL_CONVOCACAO = "2_10400_7";
	
	/** Texto do email enviado aos candidatos do processo seletivo que foram convocados. */
	public static final String TEXTO_EMAIL_CONVOCACAO = "2_10400_8";
	
	/** Assunto do email enviado aos candidatos do processo seletivo que foram cadastrados. */
	public static final String ASSUNTO_EMAIL_CONFIRMACAO_CADASTRO = "2_10400_9";
	
	/** Texto do email enviado aos candidatos do processo seletivo que foram cadastrados. */
	public static final String TEXTO_EMAIL_CONFIRMACAO_CADASTRO = "2_10400_10";
	
	/** Identificador do curso da Metrópole Digital para o qual os candidatos serão convocados. */
	public static final String ID_CURSO_METROPOLE_DIGITAL_PARA_CONVOCACAO = "2_10400_11";
	
	/**
	 * Parâmetro que contém o identificador unidade do Institudo Metrópole digital. Atualmente 6069
	 */
	public static final String ID_UNIDADE_INSTITUTO_METROPOLE_DIGITAL = "2_10400_12";
	
	/**
	 * Indica o tipo de prioridade da forma de seleção dos alunos na transferência de turma, 
	 * atualmente está disponível somente a forma aleatória.
	 * Valor: RAND 
	 */
	public static final String TIPO_ESTRATEGIA_TRANSF_TURMA = "2_10400_13";
	
}
