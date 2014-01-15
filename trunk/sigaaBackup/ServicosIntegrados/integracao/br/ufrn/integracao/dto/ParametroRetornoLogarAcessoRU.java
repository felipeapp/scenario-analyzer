package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
*
* <p>Representa um parâmetro retornado pelo método logar no sistema de acesso ao RU.</p>
*
* <p> <i> Criado porque webservices ainda não suporta trabalhar com mapas, que era a solução utilizada antes com spring remoting.</i> </p>
* 
* @author geyson
*
*/
public class ParametroRetornoLogarAcessoRU implements Serializable {
	
	/** identificador do usuário */
	public Integer idUsuario;

}
