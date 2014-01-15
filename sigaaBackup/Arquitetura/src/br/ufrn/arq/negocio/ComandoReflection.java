/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 28/05/2008
 */
package br.ufrn.arq.negocio;

import br.ufrn.arq.dominio.Comando;

/**
 * Interface para utilizar as classes de comandos na arquitetura.
 * COmo elas estão nos pacotes dos sistemas é necessário carregá-la por refletion.
 * Esta interface serve para ter uma abstração única delas na operação de recuperar
 * o comando pelo código.
 * 
 * @author Gleydson Lima
 *
 */
public interface ComandoReflection {

	public Comando getComando(int codigo);
	
}
