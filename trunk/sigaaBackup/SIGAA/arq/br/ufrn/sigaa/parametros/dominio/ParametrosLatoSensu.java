/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 19/05/2009
 *
 */
package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os par�metros do m�dulo de lato sensu.
 * @author David Pereira
 *
 */
public interface ParametrosLatoSensu {

	/***************************
	 * Lato Sensu
	 ***************************/
	public static final String TIPO_PROCESSO_CURSO_LATO = "2_10600_1";
	
	/**
	 * Define o in�cio da sequ�ncia de Registro de Diplomas no SIGAA para a gradua��o.  
	 */
	public static final String NUMERO_INICIAL_REGISTRO_DIPLOMA = "2_10600_2";
	
	/**
	 * Indica se vai ser permitido a altera��o da Grade de Hor�rios para o n�vel de ensino.
	 */
	public static final String PERMITE_ESCOLHA_GRADE_HORARIOS = "2_10600_3";
	
	/**
	 * Indica se vai ser permitido ao Gestor Lato Sensu cadastrar uma proposta de Curso Lato Sensu.
	 */
	public static final String PERMITE_GESTOR_CADASTRAR_PROPOSTA_CURSO_LATO = "2_10600_5";

	/**
	 * Indica se vai ser permitido cadastrar um discente que j� possua uma outra matr�cula ativa em outro curso de lato sensu.
	 */
	public static final String PERMITE_CADASTRAR_DISCENTE_COM_MATRICULA_ATIVA = "2_10600_6";

	/** Data no m�s para o vencimento da mensalidade do curso de lato sensu. */
	public static final String DATA_VENCIMENTO_MENSALIDADE = "2_10600_7";
	
	/** ID da configura��o padr�o da GRU para pagamentos de mensalidades. */
	public static final String ID_UNIDADE_GRU_MENSALIDADE = "2_10600_8";
	
	
	/** par�metro que cont�m o c�digo do componente curriculo que ser� obrigat�rio para todos os cursos de lato no sistema
	 * na UFRN o componente �  LAT0001 - TRABALHO FINAL DE CURSO */
	public static final String COMPONENTE_OBRIGATORIO_LATO = "2_10600_9";
	
}
