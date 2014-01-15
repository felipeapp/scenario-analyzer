/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 05/07/2006
 */
package br.ufrn.arq.email;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa um e-mail no sistema para enfileirar na fila de e-mails
 * a enviar.
 *
 * @author Gleydson Lima
 * @author David Pereira
 *
 */
public class MailBody {

	private String nome;

	private String email;

	private String assunto;

	private String mensagem;

	private String replyTo;
	
	private String confirmacaoLeitura;
	
	private String cc;
	
	private String bcc;

	private String fromName;
	
	private String contentType = HTML;

	private List<Object[]> attachments;
	
	private boolean registrarEnvio = true;
	
	public static final String HTML = "text/html";

	public static final String TEXT_PLAN = "text/plain";

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getFromName() {
		return this.fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public List<Object[]> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Object[]> attachments) {
		this.attachments = attachments;
	}

	public void addAttachment(Object[] arquivo) {
		if (attachments == null) attachments = new ArrayList<Object[]>();
		attachments.add(arquivo);
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public String getConfirmacaoLeitura() {
		return confirmacaoLeitura;
	}

	public void setConfirmacaoLeitura(String confirmacaoLeitura) {
		this.confirmacaoLeitura = confirmacaoLeitura;
	}

	public boolean isRegistrarEnvio() {
		return registrarEnvio;
	}
	
	public void setRegistrarEnvio(boolean registrarEnvio) {
		this.registrarEnvio = registrarEnvio;
	}
	
}
