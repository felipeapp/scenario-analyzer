/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.integracao.siged.dto.ArquivoDocumento;

/**
 * Data transfer object (DTO) para guardar as informações
 * de notificações. Ver classe Notificacao do SIGAdmin.
 *
 * @author David Pereira
 *
 */
public class NotificacaoDTO implements Serializable {

	/** Título da mensagem de notificação. */
	private String titulo;

	/** Mensagem de notificação. */
	private String mensagem;
	
	/** Lista de Grupos de destinatários da notificação. */
	private List<GrupoDestinatariosDTO> gruposDestinatarios;
	
	/** Lista de destinatários da notificação por email */
	private List<DestinatarioDTO> destinatarios;
	
	/** Lista de destinatários da notificação por msg do sistema */	
	private List<DestinatarioDTO> destinatariosMsg;
	
	/** Indica se o envio da notificação está autorizado. */
	private boolean autorizado;
	
	/** Nome do remetende da notificação. */
	private String nomeRemetente;
	
	/** Tipo do conteúdo da mensagem. Por exemplo: texto ou HTML. */
	private String contentType;
	
	/** Endereço de e-mail para respostas  */
	private String replyTo;
	
	/** Indica se a notificação deverá ser enviada por e-mail. */
	private boolean enviarEmail;
	
	/** Indica se a notificação deverá ser enviada por mensagem. */
	private boolean enviarMensagem;
	
	/** Arquivo a ser enviado como anexo na notificação. */
	private ArquivoDocumento anexo;

	/**
	 * Adiciona um grupo de destinatários à lista de grupos de destinatários
	 * da notificação.
	 * @param grupo
	 * @return
	 */
	public void adicionarGrupoDestinatarios(GrupoDestinatariosDTO grupo) {
		if (gruposDestinatarios == null) {
			gruposDestinatarios = new ArrayList<GrupoDestinatariosDTO>();
		}

		if (!gruposDestinatarios.contains(grupo)) {
			gruposDestinatarios.add(grupo);
		}
	}
	
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public List<GrupoDestinatariosDTO> getGruposDestinatarios() {
		return gruposDestinatarios;
	}

	public void setGruposDestinatarios(List<GrupoDestinatariosDTO> gruposDestinatarios) {
		this.gruposDestinatarios = gruposDestinatarios;
	}

	public List<DestinatarioDTO> getDestinatarios() {
		return destinatarios;
	}

	public void setDestinatarios(List<DestinatarioDTO> destinatarios) {
		this.destinatarios = destinatarios;
	}

	public boolean isAutorizado() {
		return autorizado;
	}

	public void setAutorizado(boolean autorizado) {
		this.autorizado = autorizado;
	}

	public String getNomeRemetente() {
		return nomeRemetente;
	}

	public void setNomeRemetente(String nomeRemetente) {
		this.nomeRemetente = nomeRemetente;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isEnviarEmail() {
		return enviarEmail;
	}

	public void setEnviarEmail(boolean enviarEmail) {
		this.enviarEmail = enviarEmail;
	}

	public boolean isEnviarMensagem() {
		return enviarMensagem;
	}

	public void setEnviarMensagem(boolean enviarMensagem) {
		this.enviarMensagem = enviarMensagem;
	}

	public ArquivoDocumento getAnexo() {
		return anexo;
	}

	public void setAnexo(ArquivoDocumento anexo) {
		this.anexo = anexo;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public List<DestinatarioDTO> getDestinatariosMsg() {
		return destinatariosMsg;
	}

	public void setDestinatariosMsg(List<DestinatarioDTO> destinatariosMsg) {
		this.destinatariosMsg = destinatariosMsg;
	}	

	
	
}
