/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 20/10/2009
 */
package br.ufrn.arq.negocio.validacao;

/**
 * Exceção associada ao mau uso da API para validação
 * da arquitetura.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class ValidationException extends RuntimeException {

	public ValidationException(String msg) {
		super(msg);
	}

}
