/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Created on 22/09/2009
 *
 */
package br.ufrn.sigaa.arq.jsf;

import javax.jws.WebService;

import org.springframework.stereotype.Component;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ErrorUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.biblioteca.integracao.interfaces.NotificadorErrosDesktopRemoteService;


/**
 * @author Fred_Castro
 *
 */
@WebService
@Component("notificadorErrosDesktopRemoteService")
public class NotificadorErrosDesktopRemoteServiceImpl implements NotificadorErrosDesktopRemoteService {

	public static final String JNDI_NAME = "ejb/SigaaFacade";
	

	/**
	 * Método apenas com os parâmetros necessários.
	 *
	 * @param mensagem
	 * @param loginUsuario
	 * @param nomeUsuario
	 */
	public void enviaAlerta(String mensagem, String loginUsuario, String nomeUsuario, String serverName) {
		
		// Eliminando o envio de email do desktop para os erros de time outs e envio do das mensagem do webservices
		if(  mensagem.contains("SocketTimeoutException") || mensagem.contains("Read timed out") || mensagem.contains("connect timed out") 
				|| mensagem.contains("Unexpected end of file from server") )   
			return;
		
		ErrorUtils.enviaAlerta(new Exception(mensagem), JNDI_NAME, loginUsuario, nomeUsuario, Sistema.SIGAA, SigaaSubsistemas.BIBLIOTECA.getId(), serverName);
	}
}
