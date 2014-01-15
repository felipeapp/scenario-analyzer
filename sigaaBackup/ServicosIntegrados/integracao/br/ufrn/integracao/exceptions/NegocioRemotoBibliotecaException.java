/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/11/2008
 *
 */

package br.ufrn.integracao.exceptions;

import java.io.Serializable;

/**
*
* Classe utilizada para tratar as exe��es que devem ser exibidas para o usu�rio do 
* lado remoto. N�o � poss�vel usar Exception ou RunTimeException porque sen�o pode-se mostrar 
* informa��es que o usu�rio n�o entenderia. Tamb�m n�o poderia usar NegocioException sen�o 
* teria que levar toda a arquitetura de exce��es da institui��o. Essa foi a melhor solu��o encontrada. 
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
