/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Data de Cria��o: 24/08/2010
 */
package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os par�metros do m�dulo t�cnico
 * 
 * @author Igor Linnik.
 *
 */
public interface ParametrosTecnico {

	/**
	 * Indica se vai ser permitido a altera��o da Grade de Hor�rios para o n�vel de ensino.
	 */
	public static final String PERMITE_ESCOLHA_GRADE_HORARIOS = "2_10400_1";
	
	/**
	 * Par�metro multivalorado contendo os identificadores dos cursos do Metr�pole Digital.
	 */
	public static final String ID_CURSO_METROPOLE_DIGITAL = "2_10400_4";
	
	/**
	 * Par�metro multivalorado contendo os identificadores das �nfases do m�dulo avan�ado do Metr�pole Digital.
	 */
	public static final String ID_ENFASES_MODULO_AVANCADO_METROPOLE_DIGITAL = "2_10400_5";
	
	/**
	 * Ano de ingresso dos alunos que podem efetuar matr�cula no m�dulo avan�ado.
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
	
	/** Identificador do curso da Metr�pole Digital para o qual os candidatos ser�o convocados. */
	public static final String ID_CURSO_METROPOLE_DIGITAL_PARA_CONVOCACAO = "2_10400_11";
	
	/**
	 * Par�metro que cont�m o identificador unidade do Institudo Metr�pole digital. Atualmente 6069
	 */
	public static final String ID_UNIDADE_INSTITUTO_METROPOLE_DIGITAL = "2_10400_12";
	
	/**
	 * Indica o tipo de prioridade da forma de sele��o dos alunos na transfer�ncia de turma, 
	 * atualmente est� dispon�vel somente a forma aleat�ria.
	 * Valor: RAND 
	 */
	public static final String TIPO_ESTRATEGIA_TRANSF_TURMA = "2_10400_13";
	
}
