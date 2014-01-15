/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 19/05/2009
 *
 */
package br.ufrn.sigaa.parametros.dominio;

/**
 * Interface contendo todos os parâmetros do módulo de lato sensu.
 * @author David Pereira
 *
 */
public interface ParametrosLatoSensu {

	/***************************
	 * Lato Sensu
	 ***************************/
	public static final String TIPO_PROCESSO_CURSO_LATO = "2_10600_1";
	
	/**
	 * Define o início da sequência de Registro de Diplomas no SIGAA para a graduação.  
	 */
	public static final String NUMERO_INICIAL_REGISTRO_DIPLOMA = "2_10600_2";
	
	/**
	 * Indica se vai ser permitido a alteração da Grade de Horários para o nível de ensino.
	 */
	public static final String PERMITE_ESCOLHA_GRADE_HORARIOS = "2_10600_3";
	
	/**
	 * Indica se vai ser permitido ao Gestor Lato Sensu cadastrar uma proposta de Curso Lato Sensu.
	 */
	public static final String PERMITE_GESTOR_CADASTRAR_PROPOSTA_CURSO_LATO = "2_10600_5";

	/**
	 * Indica se vai ser permitido cadastrar um discente que já possua uma outra matrícula ativa em outro curso de lato sensu.
	 */
	public static final String PERMITE_CADASTRAR_DISCENTE_COM_MATRICULA_ATIVA = "2_10600_6";

	/** Data no mês para o vencimento da mensalidade do curso de lato sensu. */
	public static final String DATA_VENCIMENTO_MENSALIDADE = "2_10600_7";
	
	/** ID da configuração padrão da GRU para pagamentos de mensalidades. */
	public static final String ID_UNIDADE_GRU_MENSALIDADE = "2_10600_8";
	
	
	/** parâmetro que contém o código do componente curriculo que será obrigatório para todos os cursos de lato no sistema
	 * na UFRN o componente é  LAT0001 - TRABALHO FINAL DE CURSO */
	public static final String COMPONENTE_OBRIGATORIO_LATO = "2_10600_9";
	
}
