/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.interfaces;

import javax.jws.WebService;

import br.ufrn.integracao.dto.PessoaDto;
import br.ufrn.integracao.dto.UsuarioDTO;
import br.ufrn.integracao.exceptions.NegocioRemotoException;

/**
 * Servi�o remoto para cadastro de usu�rios.
 * 
 * @author David Pereira
 *
 */
@WebService
public interface UsuarioRemoteService {

	public void cadastrar(UsuarioDTO cadastrador, UsuarioDTO usuario, PessoaDto pessoa) throws NegocioRemotoException;
	
}
