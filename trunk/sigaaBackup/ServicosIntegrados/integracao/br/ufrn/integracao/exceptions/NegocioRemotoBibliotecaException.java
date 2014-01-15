/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/11/2008
 *
 */

package br.ufrn.integracao.exceptions;

import java.io.Serializable;

/**
*
* Classe utilizada para tratar as exeções que devem ser exibidas para o usuário do 
* lado remoto. Não é possível usar Exception ou RunTimeException porque senão pode-se mostrar 
* informações que o usuário não entenderia. Também não poderia usar NegocioException senão 
* teria que levar toda a arquitetura de exceções da instituição. Essa foi a melhor solução encontrada. 
*
* @author jadson
* @since 18/11/2008
* @version 1.0 criacao da classe
*
*/
public class NegocioRemotoBibliotecaException extends Exception implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int idMaterial;
	
	public NegocioRemotoBibliotecaException(String mensagem){
		super(mensagem);
	}
	
	public NegocioRemotoBibliotecaException(String mensagem, int idMaterial){
		super(mensagem);
		this.idMaterial = idMaterial;
	}

	public int getIdMaterial() {
		return idMaterial;
	}

	public void setIdMaterial(int idMaterial) {
		this.idMaterial = idMaterial;
	}
}
