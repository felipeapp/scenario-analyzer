/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 15/07/2009
 */
package br.ufrn.arq.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação a ser usada em um campo e que
 * indica que o campo é utilizado para sinalizar se a entidade
 * está ativa ou não.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CampoAtivo {

	boolean value() default true;
	
}
