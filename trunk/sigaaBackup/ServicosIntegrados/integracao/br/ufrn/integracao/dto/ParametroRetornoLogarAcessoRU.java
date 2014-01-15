package br.ufrn.integracao.dto;

import java.io.Serializable;

/**
*
* <p>Representa um par�metro retornado pelo m�todo logar no sistema de acesso ao RU.</p>
*
* <p> <i> Criado porque webservices ainda n�o suporta trabalhar com mapas, que era a solu��o utilizada antes com spring remoting.</i> </p>
* 
* @author geyson
*
*/
public class ParametroRetornoLogarAcessoRU implements Serializable {
	
	/** identificador do usu�rio */
	public Integer idUsuario;

}
