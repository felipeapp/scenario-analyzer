/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 25/04/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.dominio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.comum.dominio.notificacoes.Notificacao;

/**
 * Classe que representa uma notifica��o de orienta��o acad�mica.
 * Utilizada para armazenar o {@link DestinatarioMensagemOrientacao} relacionado.
 * 
 * @author bernardo
 *
 */
public class NotificacaoOrientacaoAcademica extends Notificacao {

	public NotificacaoOrientacaoAcademica() {
		super();
	}

	/** Cole��o de {@link DestinatarioMensagemOrientacao} que receber�o a notifica��o. */
	private Collection<DestinatarioMensagemOrientacao> destinatariosMensagemOrientacao;
	
	/**
	 * Adiciona um {@link DestinatarioMensagemOrientacao} na listagem da notifica��o.
	 * 
	 * @param destinatario
	 */
	public void addDestinatarioMensagemOrientacao(DestinatarioMensagemOrientacao destinatario) {
		if(isEmpty(destinatariosMensagemOrientacao))
			destinatariosMensagemOrientacao = new ArrayList<DestinatarioMensagemOrientacao>();
		
		destinatariosMensagemOrientacao.add(destinatario);
	}

	public Collection<DestinatarioMensagemOrientacao> getDestinatariosMensagemOrientacao() {
		return destinatariosMensagemOrientacao;
	}
}
