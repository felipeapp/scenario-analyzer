/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 23/05/2011
 * 
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
*
* <p>Representa um parâmetro retornado pelo método logar no desktop de cadastro de pessoas/digitais.</p>
*
* <p> <i> Criado porque webservices ainda não suporta trabalhar com mapas, que era a solução utilizada antes com spring remoting.</i> </p>
* 
* @author geyson
*
*/
public class ParametrosRetornoLogarCadastroPessoaDTO implements Serializable {
	
	/** identificador do usuário */
	public Integer idUsuario;
	
	/** tipo de permissão  */
	public String permissaoCadastroPessoa;
	

}
