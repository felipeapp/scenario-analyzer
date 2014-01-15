/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 13/01/2010
 */
package br.ufrn.arq.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.parametrizacao.ParametroHelper;

/**
 * Classe utilitária para recebimento de emails
 * na arquitetura.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class ReceiveMail {

	public static List<ReceivedEmail> receive() throws MessagingException, IOException {
		String host = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.HOST_RECEBIMENTO_EMAIL);
		String username = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.USERNAME_RECEBIMENTO_EMAIL);
		String password = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.PASSWORD_RECEBIMENTO_EMAIL);

		// Create empty properties
		Properties props = new Properties();

		// Get session
		Session session = Session.getDefaultInstance(props, null);

		// Get the store
		Store store = session.getStore("imap");
		store.connect(host, username, password);

		// Get folder
		Folder folder = store.getFolder("INBOX");
		folder.open(Folder.READ_WRITE);

		// Get directory
		Message[] messages = folder.getMessages();
		List<ReceivedEmail> received = new ArrayList<ReceivedEmail>();
		for (Message msg : messages) {
			ReceivedEmail re = new ReceivedEmail();
			re.setContentType(msg.getContentType());
			re.setSubject(msg.getSubject());
			re.setFrom(msg.getFrom()[0].toString());
			
			if (msg.getContent() instanceof MimeMultipart) {
				Multipart mp = (Multipart) msg.getContent();
				for (int i = 0; i < mp.getCount(); i++) {
					if (mp.getBodyPart(i).getContent() instanceof MimeMultipart) {
						re.addAttachments(mp.getBodyPart(i).getInputStream());
					} else {
						re.setMessage(mp.getBodyPart(i).getContent().toString());
					}
				}
			}

			received.add(re);
		}
		

		// Close connection 
		folder.close(false);
		store.close();

		return received;
	}
		
}
