/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 05/07/2006
 */
package br.ufrn.arq.email;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * Classe usada para gerar e-mail a partir de templates
 * 
 * @author Gleydson Lima
 *
 */
public class MailGenerator {
	private static MailGenerator singleton;

	private static ResourceBundle mensagens;

	private MailGenerator() {
		mensagens = ResourceBundle.getBundle("br.ufrn.sipac.arq.util.Mail");
	}
	
	public static MailGenerator getInstance() {
		if (singleton == null)
			singleton = new MailGenerator();
		
		return singleton;
	}

	public String getMensagem(String messageKey, Object... args) {
		String mensagem = getString(messageKey);

		if (args != null && args.length > 0)
			mensagem = MessageFormat.format(mensagem, args);

		return mensagem;
	}
	
	public String getString(String key) {
		try {
			return mensagens.getString(key);
		} catch (MissingResourceException e) {
			Mail.sendAlertaAdmin("Erro ao obter mensagem com chave " + key, e);
			return '!' + key + '!';
		}
	}
}
