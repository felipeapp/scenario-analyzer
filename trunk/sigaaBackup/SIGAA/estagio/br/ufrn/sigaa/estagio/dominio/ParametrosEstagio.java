/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Data de Cria��o: 25/10/2010
 */
package br.ufrn.sigaa.estagio.dominio;

/**
 * Interface contendo todos os par�metros do m�dulo de Est�gio
 * 
 * @author Arlindo Rodrigues
 *
 */
public interface ParametrosEstagio {
	
	/** Carga Hor�ria M�xima de Est�gio que alterna entre Teoria e Pr�tica */
	public static final String CARGA_HORARIA_MAX_ESTAGIO_TEORIA_PRATICA = "2_23100_1";
	
	/** Carga Hor�ria M�xima de Est�gio */
	public static final String CARGA_HORARIA_MAX_ESTAGIO = "2_23100_2";
	
	/** Per�odo M�ximo de Est�gio em Anos */
	public static final String PERIODO_MAX_ESTAGIO = "2_23100_3";
	
	/** Per�odo m�nimo de Est�gio em Meses */
	public static final String PERIODO_MIN_ESTAGIO = "2_23100_4";
	
	/** Indica o M�ximo de Estagi�rios que um Supervisor pode ter */
	public static final String MAXIMO_ESTAGIARIOS_POR_SUPERVISOR = "2_23100_5";	
	
	/** Quantidade de Meses para o preenchimento do Relat�rio de Est�gio Peri�dico */
	public static final String MESES_PARA_PREENCHIMENTO_RELATORIO_ESTAGIO = "2_23100_6"; 
	
	/** Valor m�nimo a receber de bolsa por est�gio n�o obrigat�rio. */
	public static final String VALOR_MINIMO_BOLSA_ESTAGIO_NAO_OBRIGATORIO= "2_23100_7";
	
	/** Valor m�nimo a receber de auxilio trasnporte por dia no est�gio n�o obrigat�rio. */
	public static final String VALOR_MINIMO_TRANSPORTE_ESTAGIO_NAO_OBRIGATORIO = "2_23100_8";
	

}
