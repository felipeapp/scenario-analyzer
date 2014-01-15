/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 20/10/2009
 */
package br.ufrn.arq.negocio.validacao.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para ser colocada em atributos
 * de classes de domínio e que é utilizada 
 * para validar se o campo é um CEP válido.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cep {

}
