/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 20/07/2010
 */
package br.ufrn.integracao.interfaces;

import javax.jws.WebService;

/**
 * Servi�o remoto para ativar ou inativar um usu�rio
 * em um sistema.
 *
 * @author David Pereira
 *
 */
@WebService
public interface PermissaoUsuarioSistemaRemoteService {

	public void configurarPermissaoUsuario(int idUsuario, Integer sistema, char permissao);
	
}
