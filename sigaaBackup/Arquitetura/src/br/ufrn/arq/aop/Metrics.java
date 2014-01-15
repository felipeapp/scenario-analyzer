/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 29/07/2009
 */
package br.ufrn.arq.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação que indica que um método específico
 * ou todos os métodos da classe serão medidos
 * de acordo com os critérios definidos em {@link MetricType}. 
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD}) 
@Retention(RetentionPolicy.RUNTIME)
public @interface Metrics {

	MetricType[] value();
	
}
