/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '20/04/2010'
 *
 */
package br.ufrn.sigaa.pesquisa.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.jsf.NotificacoesMBean;

/**
 * MBean respons�vel por redirecionar o usu�rio ao formul�rio de envio de mensagem ao bolsista
 * 
 * @author Thalisson Muriel
 *
 */
@Component("enviarMsgBolsista") @Scope("request")
public class EnviarMensagemBolsistaMBean extends NotificacoesMBean {
	
	/**
	 * Direciona o usu�rio para a p�gina onde cont�m o formul�rio de envio da mensagem ao bolsista. <br>
	 * JSP: N�o invocado por JSP
	 */
	public String iniciar() throws ArqException {
		return redirect(getFormPage());
	}
	
	
	/**
	 * Define a p�gina que cont�m o formul�rio de envio de mensagem ao bolsista.<br>
	 * JSP: N�o invocado por JSP
	 */
	@Override
	public String getFormPage() {
		return "/geral/mensagem/enviar_mensagem_bolsista.jsf";
	}	
	
	
	/**
	 * Direciona o usu�rio para a p�gina onde cont�m o formul�rio. <br>
	 * JSP: N�o invocado por JSP
	 */
	public String enviarMensagem() throws ArqException {
		return redirect(getFormPage());
	}
	
}
