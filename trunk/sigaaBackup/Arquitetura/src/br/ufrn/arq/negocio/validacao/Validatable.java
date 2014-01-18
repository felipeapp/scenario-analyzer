/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/11/2007
 */
package br.ufrn.arq.negocio.validacao;

import br.ufrn.arq.dominio.PersistDB;


/**
 * Interface que as classes de dom�nio devem implementar
 * usada em valida��o de CRUD
 *
 * @author Gleydson Lima
 *
 */
public interface Validatable extends PersistDB {

	public ListaMensagens validate();

}