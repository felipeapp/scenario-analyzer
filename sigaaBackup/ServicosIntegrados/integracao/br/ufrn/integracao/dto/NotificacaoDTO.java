/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
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
 * Data transfer object (DTO) para guardar as informa��es
 * de notifica��es. Ver classe Notificacao do SIGAdmin.
 *
 * @author David Pereira
 *
 */
public class NotificacaoDTO implements Serializable {

	/** T�tulo da mensagem de notifica��o. */
	private String titulo;

	/** Mensagem de notifica��o. */
	private String mensagem;
	
	/** Lista de Grupos de destinat�rios da notifica��o. */
	private List<GrupoDestinatariosDTO> gruposDestinatarios;
	
	/** Lista de destinat�rios da notifica��o por email */
	private List<DestinatarioDTO> destinatarios;
	
	/** Lista de destinat�rios da notifica��o por msg do sistema */	
	private List<DestinatarioDTO> destinatariosMsg;
	
	/** Indica se o envio da notifica��o est� autorizado. */
	private boolean autorizado;
	
	/** Nome do remetende da notifica��o. */
	private String nomeRemetente;
	
	/** Tipo do conte�do da mensagem. Por exemplo: texto ou HTML. */
	private String contentType;
	
	/** Endere�o de e-mail para respostas  */
	private String replyTo;
	
	/** Indica se a notifica��o dever� ser enviada por e-mail. */
	private boolean enviarEmail;
	
	/** Indica se a notifica��o dever� ser enviada por mensagem. */
	private boolean enviarMensagem;
	
	/** Arquivo a ser enviado como anexo na notifica��o. */
	private ArquivoDocumento anexo;

	/**
	 * Adiciona um grupo de destinat�rios � lista de grupos de destinat�rios
	 * da notifica��o.
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
