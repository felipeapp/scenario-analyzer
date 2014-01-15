/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Data de Criação: 25/10/2010
 */
package br.ufrn.sigaa.estagio.dominio;

/**
 * Interface contendo todos os parâmetros do módulo de Estágio
 * 
 * @author Arlindo Rodrigues
 *
 */
public interface ParametrosEstagio {
	
	/** Carga Horária Máxima de Estágio que alterna entre Teoria e Prática */
	public static final String CARGA_HORARIA_MAX_ESTAGIO_TEORIA_PRATICA = "2_23100_1";
	
	/** Carga Horária Máxima de Estágio */
	public static final String CARGA_HORARIA_MAX_ESTAGIO = "2_23100_2";
	
	/** Período Máximo de Estágio em Anos */
	public static final String PERIODO_MAX_ESTAGIO = "2_23100_3";
	
	/** Período mínimo de Estágio em Meses */
	public static final String PERIODO_MIN_ESTAGIO = "2_23100_4";
	
	/** Indica o Máximo de Estagiários que um Supervisor pode ter */
	public static final String MAXIMO_ESTAGIARIOS_POR_SUPERVISOR = "2_23100_5";	
	
	/** Quantidade de Meses para o preenchimento do Relatório de Estágio Periódico */
	public static final String MESES_PARA_PREENCHIMENTO_RELATORIO_ESTAGIO = "2_23100_6"; 
	
	/** Valor mínimo a receber de bolsa por estágio não obrigatório. */
	public static final String VALOR_MINIMO_BOLSA_ESTAGIO_NAO_OBRIGATORIO= "2_23100_7";
	
	/** Valor mínimo a receber de auxilio trasnporte por dia no estágio não obrigatório. */
	public static final String VALOR_MINIMO_TRANSPORTE_ESTAGIO_NAO_OBRIGATORIO = "2_23100_8";
	

}
