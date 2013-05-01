package br.com.ecommerce.arq.listeners;

import java.util.ArrayList;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import br.com.ecommerce.arq.dominio.Mensagem;

/**
 * Listener para a sess�o do usu�rio.
 * @author Rodrigo Dutra de Oliveira
 *
 */
public class SessionListener implements HttpSessionListener{

	public void sessionCreated(HttpSessionEvent se) {
		//Mensagens que poder�o vir a ser exibidas para o usu�rio.
		se.getSession().setAttribute("mensagens", new ArrayList<Mensagem>());
	}

	/**
	 * Efetua as opera��es necess�rias para o fechamento da sess�o com o usu�rio.
	 */
	public void sessionDestroyed(HttpSessionEvent arg0) {

	}

}
