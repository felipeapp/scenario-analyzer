/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/11/2008
 *
 */

package br.ufrn.sigaa.biblioteca.integracao.exceptions;

import java.io.Serializable;

/**
*
* <p> Classe utilizada para tratar as exeções que devem ser exibidas para o usuário na aplicação desktop. </p> 
*  
* <p>Não pode ser utilizado NegocioException da arquitetura. </p> 
*
* @author jadson
* @since 18/11/2008
* @version 1.0 criacao da classe
*
*/
public class NegocioRemotoBibliotecaDesktopException extends Exception implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/**  O materiais que gerou a exceção */
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

	/** Obrigatório por causa do Web-Service */
	public void setIdMaterialComproblema(int idMaterialComproblema) {
		this.idMaterialComproblema = idMaterialComproblema;
	}
	
	
	
}
