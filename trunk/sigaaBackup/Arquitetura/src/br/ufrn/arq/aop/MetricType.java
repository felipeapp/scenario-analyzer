/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 29/07/2009
 */
package br.ufrn.arq.aop;

/**
 * Define os tipos de métricas que serão utilizadas
 * no profiling de métodos de classes.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public enum MetricType {

	TIME, // Média de tempo de execução
	
	COUNT_CALL, // Número total de chamadas ao método
	
	MAX_TIME // Tempo máximo de execução do método
	
}
