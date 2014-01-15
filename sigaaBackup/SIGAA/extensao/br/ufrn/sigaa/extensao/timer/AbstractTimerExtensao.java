/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 03/05/2012
 * 
 */
package br.ufrn.sigaa.extensao.timer;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.NetworkUtils;

/**
 * Classe que contém os métodos comuns para os timers de extesão.
 * 
 * @author jadson
 *
 */
public abstract class AbstractTimerExtensao extends TarefaTimer{
	
	/**
	 * Envia um email com a informação do erro na execuçção da rotina para os administradores do sistema.
	 *
	 * @param siglaSigaa
	 * @param assuntoEmail
	 * @param e
	 */
	protected void enviaEmailNotificacaoAdministradorSistema(final String descricaoOperacao, final String... mensagemConfirmacao){
		
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String siglaSigaa = RepositorioDadosInstitucionais.get("siglaSigaa");
		
		String assunto = "["+siglaSigaa+"] "+descricaoOperacao+" - EXECUTADO EM " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date());
		
		StringBuilder mensagem = new StringBuilder("Server: " + NetworkUtils.getLocalName() + "<br>");
		mensagem.append( "<br><br>");
		for (int i = 0; i < mensagemConfirmacao.length; i++) {
			mensagem.append(mensagemConfirmacao[i]+"<br>");
		}
		mensagem.append( "<br><br>");
		
		MailBody mail = new MailBody();
		mail.setEmail(email);
		mail.setAssunto(assunto);
		mail.setMensagem(mensagem.toString());
		
		Mail.send(mail);
	}
	

	/**
	 * Envia um email com a informação do erro na execuçção da rotina para os administradores do sistema.
	 *
	 * @param siglaSigaa
	 * @param assuntoEmail
	 * @param e
	 */
	protected void enviaEmailErroAdministradorSistema(String descricaoOperacao, String descricaoErro, Exception e){
		String siglaSigaa = RepositorioDadosInstitucionais.get("siglaSigaa");
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto =   " Erro "+siglaSigaa+" - "+descricaoOperacao+": " + descricaoErro;
		String mensagem =  " Server: " + NetworkUtils.getLocalName() + "<br>" +
		e.getMessage() + "<br><br><br>" 
		+ Arrays.toString(e.getStackTrace()).replace(",", "\n") + (e.getCause() != null ? Arrays.toString(e.getCause().getStackTrace()).replace(",", "\n") : "");
		
		MailBody mail = new MailBody();
		mail.setEmail( email );
		mail.setAssunto(assunto);
		mail.setMensagem( mensagem );
		Mail.send(mail);
	}
	
}
