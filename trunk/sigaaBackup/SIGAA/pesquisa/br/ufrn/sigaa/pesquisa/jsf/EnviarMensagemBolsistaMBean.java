/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * MBean responsável por redirecionar o usuário ao formulário de envio de mensagem ao bolsista
 * 
 * @author Thalisson Muriel
 *
 */
@Component("enviarMsgBolsista") @Scope("request")
public class EnviarMensagemBolsistaMBean extends NotificacoesMBean {
	
	/**
	 * Direciona o usuário para a página onde contém o formulário de envio da mensagem ao bolsista. <br>
	 * JSP: Não invocado por JSP
	 */
	public String iniciar() throws ArqException {
		return redirect(getFormPage());
	}
	
	
	/**
	 * Define a página que contém o formulário de envio de mensagem ao bolsista.<br>
	 * JSP: Não invocado por JSP
	 */
	@Override
	public String getFormPage() {
		return "/geral/mensagem/enviar_mensagem_bolsista.jsf";
	}	
	
	
	/**
	 * Direciona o usuário para a página onde contém o formulário. <br>
	 * JSP: Não invocado por JSP
	 */
	public String enviarMensagem() throws ArqException {
		return redirect(getFormPage());
	}
	
}
