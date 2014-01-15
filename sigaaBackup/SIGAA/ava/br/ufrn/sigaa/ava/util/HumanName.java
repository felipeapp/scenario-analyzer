/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 22/01/2008
 */
package br.ufrn.sigaa.ava.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Nome de entidades para ser usado nos cadastros.
 * 
 * @author David Pereira
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HumanName {

	String value();
	
	char genero();
	
}
