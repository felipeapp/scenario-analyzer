/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/01/2010
 */
package br.ufrn.arq.email;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe para armazenar informações de emails
 * recebidos pela arquitetura.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class ReceivedEmail {

	/** Content type do e-mail, se texto ou HTML */
	private String contentType;
	
	/** Assunto do e-mail */
	private String subject;
	
	/** Texto do e-mail */
	private String message;
	
	/** Anexos do e-mail */
	private List<InputStream> attachments;
	
	/** Remetente do e-mail */
	private String from;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<InputStream> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<InputStream> attachments) {
		this.attachments = attachments;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void addAttachments(InputStream file) {
		if (attachments == null)
			attachments = new ArrayList<InputStream>();
		attachments.add(file);
	}

	
}
