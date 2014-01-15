

/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Created on 22/09/2009
 *
 */
package br.ufrn.sigaa.biblioteca.integracao.interfaces;

import javax.jws.WebService;

/**
 * @author Fred_Castro
 * @version 1.1 Jadson 20/05/2011 o serviço virou um webservice
 *
 */
@WebService
public interface NotificadorErrosDesktopRemoteService {

	public void enviaAlerta(String mensagem, String loginUsuario, String nomeUsuario, String serverName);
	
}
