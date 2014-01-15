/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 20/07/2010
 */
package br.ufrn.integracao.interfaces;

import javax.jws.WebService;

/**
 * Serviço remoto para ativar ou inativar um usuário
 * em um sistema.
 *
 * @author David Pereira
 *
 */
@WebService
public interface PermissaoUsuarioSistemaRemoteService {

	public void configurarPermissaoUsuario(int idUsuario, Integer sistema, char permissao);
	
}
