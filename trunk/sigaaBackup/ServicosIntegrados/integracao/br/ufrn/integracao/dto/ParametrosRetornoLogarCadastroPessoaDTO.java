/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 23/05/2011
 * 
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
*
* <p>Representa um par�metro retornado pelo m�todo logar no desktop de cadastro de pessoas/digitais.</p>
*
* <p> <i> Criado porque webservices ainda n�o suporta trabalhar com mapas, que era a solu��o utilizada antes com spring remoting.</i> </p>
* 
* @author geyson
*
*/
public class ParametrosRetornoLogarCadastroPessoaDTO implements Serializable {
	
	/** identificador do usu�rio */
	public Integer idUsuario;
	
	/** tipo de permiss�o  */
	public String permissaoCadastroPessoa;
	

}
