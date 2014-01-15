/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/11/2008
 *
 */

package br.ufrn.sigaa.biblioteca.integracao.exceptions;

import java.io.Serializable;

/**
*
* <p> Classe utilizada para tratar as exe��es que devem ser exibidas para o usu�rio na aplica��o desktop. </p> 
*  
* <p>N�o pode ser utilizado NegocioException da arquitetura. </p> 
*
* @author jadson
* @since 18/11/2008
* @version 1.0 criacao da classe
*
*/
public class NegocioRemotoBibliotecaDesktopException extends Exception implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**  O materiais que gerou a exce��o */
	private int idMaterialComproblema;
	
	public NegocioRemotoBibliotecaDesktopException(String mensagem){
		super(mensagem);
	}
	
	public NegocioRemotoBibliotecaDesktopException(String mensagem, int idMaterialComproblema){
		super(mensagem);
		this.idMaterialComproblema =  idMaterialComproblema;
	}

	public int getIdMaterialComproblema() {
		return idMaterialComproblema;
	}

	/** Obrigat�rio por causa do Web-Service */
	public void setIdMaterialComproblema(int idMaterialComproblema) {
		this.idMaterialComproblema = idMaterialComproblema;
	}
	
	
	
}
