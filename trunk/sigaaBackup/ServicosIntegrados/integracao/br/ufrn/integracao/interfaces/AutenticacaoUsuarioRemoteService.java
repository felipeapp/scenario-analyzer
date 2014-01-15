/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 13/04/2010
 */
package br.ufrn.integracao.interfaces;

import java.util.List;

import javax.jws.WebService;

import br.ufrn.integracao.dto.UsuarioDTO;
import br.ufrn.integracao.dto.PermissaoDTO;

/**
 * Serviço remoto para autenticação de usuários
 * em aplicações Desktop.
 * 
 * @author David Pereira
 *
 */
@WebService
public interface AutenticacaoUsuarioRemoteService {

	public boolean autenticaUsuario(String login, String senha);
	
	public UsuarioDTO carregaInfoUsuario(String login);
	
	public List<PermissaoDTO> carregaPermissoes(UsuarioDTO usuario);
	
}
