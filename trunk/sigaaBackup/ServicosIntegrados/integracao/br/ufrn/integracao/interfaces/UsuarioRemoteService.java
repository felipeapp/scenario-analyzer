/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
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
 * Serviço remoto para cadastro de usuários.
 * 
 * @author David Pereira
 *
 */
@WebService
public interface UsuarioRemoteService {

	public void cadastrar(UsuarioDTO cadastrador, UsuarioDTO usuario, PessoaDto pessoa) throws NegocioRemotoException;
	
}
