/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 06/11/2007
 */
package br.ufrn.arq.negocio.validacao;

import br.ufrn.arq.dominio.PersistDB;


/**
 * Interface que as classes de domínio devem implementar
 * usada em validação de CRUD
 *
 * @author Gleydson Lima
 *
 */
public interface Validatable extends PersistDB {

	public ListaMensagens validate();

}
