/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendencia de Informatica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/11/2008
 *
 */

package br.ufrn.sigaa.biblioteca.integracao.exceptions;

import java.io.Serializable;

/**
*
* <p>Classe utilizada para tratar as execoes que devem ser exibidas para o usuario na aplicacao do coletor. </p> 
*  
* <p>Nao pode ser utilizado NegocioException da arquitetura. </p> 
*
* <strong>OBSERVACAO 1 .:</strong>   A VM do coletor trabalha na versao 1.3 do Java.
* <strong>OBSERVACAO 2 .:</strong>   Nao usar acentos nos comentarios porque essas classes sao copiadas para o 
* netbens e dar erro de codificacao se usar.
*
* @author felipe
* @since 04/04/2012
* @version 1.0 criacao da classe
*
*/
public class NegocioRemotoBibliotecaColetorException extends Exception implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	public NegocioRemotoBibliotecaColetorException(String mensagem){
		super(mensagem);
	}
	
}
