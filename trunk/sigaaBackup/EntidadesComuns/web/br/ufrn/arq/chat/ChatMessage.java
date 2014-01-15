package br.ufrn.arq.chat;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe de domínio que representa uma mensagem enviada ou recebida do chat
 * 
 * @author Gleydson
 * 
 */
public class ChatMessage implements Serializable, Comparable<ChatMessage> {

	private static final long serialVersionUID = 1L;

	private long idMessage;

	private int idChat;

	private String mensagem;

	private int idUsuario;

	private String nomeUsuario;

	private Integer idFoto;

	private Date data;

	public long getIdMessage() {
		return idMessage;
	}

	public void setIdMessage(long idMessage) {
		this.idMessage = idMessage;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public Integer getIdFoto() {
		return idFoto;
	}

	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}

	public int getIdChat() {
		return idChat;
	}

	public void setIdChat(int idChat) {
		this.idChat = idChat;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int compareTo(ChatMessage o) {
		return (int) (idMessage - o.getIdMessage());
	}

}