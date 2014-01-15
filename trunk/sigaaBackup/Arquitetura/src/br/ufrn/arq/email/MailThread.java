/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 10/07/2006
 */
package br.ufrn.arq.email;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Thread para consumir e-mails da thread de e-mails 
 * e enviá-los por um servidor SMTP.
 *  
 * @author Gleydson Lima
 * @author David Pereira
 *
 */
public class MailThread extends Thread {

	private String host = null;

	private String from = null;
	
	private String reply = null;

	private String fromName = null;

	private String username = null;

	private String password = null;
	
	private boolean ssl = false;
	
	private boolean autenticacao = false;
	
	private String porta = null;
	
	private static Session session;
	
	private FacadeDelegate facade;

	public MailThread(FacadeDelegate facade) {
		
		this.facade = facade;
		
		Map<String, String> paramMail = ParametroHelper.getInstance().getParametroMap(ConstantesParametroGeral.CONFIGURACOES_ENVIO_EMAIL); 
		host = paramMail.get("host");
		username = paramMail.get("usuario");
		password = paramMail.get("senha");
		porta = paramMail.get("porta");
		from = paramMail.get("enderecoRemetente"); 
		fromName = paramMail.get("nomeRemetente");
		reply = paramMail.get("enderecoResposta");
		ssl = Boolean.valueOf(paramMail.get("usarSsl"));
		autenticacao = Boolean.valueOf(paramMail.get("autenticacao"));
		
	}

	@Override
	public void run() {
		while (true) {
			
			MailBody mail = null;
			synchronized (Mail.queue) {
				mail = Mail.queue.poll();
				if (mail == null) {
					try {
						Mail.queue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					Mail.msgDelivery++;
				}
			}
			delivery(mail);
		}
		
	}

	private void delivery(MailBody mail) {
		
		List<File> arquivosAnexos = null;
		
		try {
			
			if ( mail == null ) { 
				return;
			}
			
			Session session = getSession();

			// Mensagem
			MimeMessage message = new MimeMessage(session);

			String fromName = this.fromName;
			if (mail.getFromName() != null) {
				fromName = mail.getFromName();
			}
			message.setFrom(new InternetAddress(from, fromName));
			
			// Definir email para respostas
			String replyTo = this.reply;
			if (mail.getReplyTo() != null && !mail.getReplyTo().trim().equals("")) {
				replyTo = mail.getReplyTo();
			}
			message.setReplyTo( new InternetAddress[] { new InternetAddress(replyTo) } );

			if (!isEmpty(mail.getConfirmacaoLeitura()))
				message.setHeader("Return-Receipt-To", mail.getConfirmacaoLeitura());
			
			MimeMultipart content = new MimeMultipart();
			MimeBodyPart text = new MimeBodyPart();
			MimeBodyPart html = new MimeBodyPart();

			text.setText(StringUtils.stripHtmlTags(mail.getMensagem()));
			text.setHeader("MIME-Version", "1.0");
			text.setHeader("Content-Type", text.getContentType());

			html.setContent(mail.getMensagem(), mail.getContentType());
			html.setHeader("MIME-Version", "1.0");
			html.setHeader("Content-Type", mail.getContentType());

			if (mail.getMensagem().indexOf("<") != -1 && mail.getMensagem().indexOf(">") != -1) {
				content.addBodyPart(html);
			} else {
				content.addBodyPart(text);
			}
			
			if (!isEmpty(mail.getAttachments())) {
				arquivosAnexos = new ArrayList<File>(mail.getAttachments().size());
				
				for (Object[] f : mail.getAttachments()) {
					MimeBodyPart anexo = new MimeBodyPart();
					
					File tmp = File.createTempFile("anexo_" + System.currentTimeMillis(), ".tmp");
					FileUtils.writeByteArrayToFile(tmp, (byte[]) f[1]);
					
					anexo.setDataHandler(new DataHandler(new FileDataSource(tmp)));
					anexo.setFileName((String) f[0]);
					content.addBodyPart(anexo);
					
					arquivosAnexos.add(tmp);
				}
			}

			message.setContent(content);
			message.setHeader("MIME-Version", "1.0");
			message.setHeader("X-Mailer", "Recommend-It Mailer V2.03c02");
			message.setSentDate(new Date());

			if (mail.getEmail() !=null && mail.getEmail().indexOf(";") != -1) {
				StringTokenizer st = new StringTokenizer(mail.getEmail(), ";");
				
				while (st.hasMoreTokens()) {
					try {
						message.addRecipient(Message.RecipientType.TO, new InternetAddress(st.nextToken(), mail.getNome()));
					} catch (Exception e) {
						// No caso de um dos destinos ser inválido 
					}
				}
			} else if (mail.getEmail() != null){
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(mail.getEmail(), mail.getNome()));
			}
			
			if (mail.getCc() !=null && mail.getCc().indexOf(";") != -1) {
				StringTokenizer st = new StringTokenizer(mail.getCc(), ";");
				
				while (st.hasMoreTokens()) {
					try {
						message.addRecipient(Message.RecipientType.CC, new InternetAddress(st.nextToken(), mail.getNome()));
					} catch (Exception e) {
						// No caso de um dos destinos ser inválido 
					}
				}
			} else if(mail.getCc()!=null){
				message.addRecipient(Message.RecipientType.CC, new InternetAddress(mail.getEmail(), mail.getNome()));
			}
			
			
			if (mail.getBcc() != null && mail.getBcc().indexOf(";") != -1) {
				StringTokenizer st = new StringTokenizer(mail.getBcc(), ";");
				
				while (st.hasMoreTokens()) {
					try {
						message.addRecipient(Message.RecipientType.BCC, new InternetAddress(st.nextToken(), mail.getNome()));
					} catch (Exception e) {
						// No caso de um dos destinos ser inválido 
					}
				}
			} else if(mail.getBcc() != null){
				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(mail.getBcc(), mail.getNome()));
			}
			
			message.setSubject(mail.getAssunto());

			// Envia
			
			try {
				enviar(session, message);
				
				if (mail.isRegistrarEnvio()) {
					registrarEnvioEmail(mail);
				}
			} catch(Exception e) {
				registrarErroEnvio(mail, e);
			}
			
		} catch (Exception e) {
			System.err.println("Erro ao enviar email");
			e.printStackTrace();
		} finally {
			if (!isEmpty(arquivosAnexos)) {
				for (File arquivo : arquivosAnexos) {
					arquivo.delete();
				}
			}
		}
	}

	private void enviar(Session session, MimeMessage message) throws NoSuchProviderException, MessagingException {
		Transport transport = session.getTransport("smtp");
		transport.connect(host, username, password);
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
	}

	private Session getSession() {
		if(session==null){
			Properties props = System.getProperties();
				
			// Coloca o servidor de E-mail
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.port", porta);
		
			if (ssl) {
				props.put("mail.smtp.socketFactory.port", porta);
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			} 
			
			if (autenticacao){
				props.put("mail.smtp.auth", "true");
				
				session = Session.getInstance(props, new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});
			}else {
				session = Session.getInstance(props, null);
			}
		
			session.setDebug(true);
		}

		return session;
	}

	private void registrarEnvioEmail(MailBody mail) throws NegocioException, ArqException {
		
		EmailEnviado email = new EmailEnviado();
		email.setAssunto(mail.getAssunto());
		email.setConteudo(mail.getMensagem());
		email.setEnviadoEm(new Date());
		email.setParaEmail(mail.getEmail());
		email.setParaNome(mail.getNome());
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(email);
		mov.setCodMovimento(ArqListaComando.CADASTRAR);
		
		facade.execute(mov, new UsuarioGeral(UsuarioGeral.USUARIO_SISTEMA), Sistema.COMUM);
		
	}

	private void registrarErroEnvio(MailBody mail, Exception e) throws NegocioException, ArqException {
		
		ErroEnvioEmail email = new ErroEnvioEmail();
		email.setAssunto(mail.getAssunto());
		email.setConteudo(mail.getMensagem());
		email.setEnviadoEm(new Date());
		email.setParaEmail(mail.getEmail());
		email.setParaNome(mail.getNome());

		StringBuilder s = new StringBuilder();
		s.append("Exceção: ");
		s.append(e.toString());
		s.append("\n\n");
		s.append(Arrays.toString(e.getStackTrace()));

		email.setExcecao(s.toString());
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(email);
		mov.setCodMovimento(ArqListaComando.CADASTRAR);
		
		facade.execute(mov, new UsuarioGeral(UsuarioGeral.USUARIO_SISTEMA), Sistema.COMUM);
		//Define a sessão como null em caso de erro, para o método getSession recriar.
		session = null;
		
	}

	
}