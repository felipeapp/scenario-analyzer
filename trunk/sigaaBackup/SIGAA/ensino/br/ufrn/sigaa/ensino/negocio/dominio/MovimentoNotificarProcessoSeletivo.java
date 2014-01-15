package br.ufrn.sigaa.ensino.negocio.dominio;

import java.util.Map;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.NotificacaoProcessoSeletivo;
/**
 * Movimento utilizado durante o cadastro de uma notifica��o de processo seletivo.
 * 
 * 
 * @author M�rio Rizzi
 *
 */
public class MovimentoNotificarProcessoSeletivo extends AbstractMovimentoAdapter {
	
	/** Objeto a ser persistido	 */
	private NotificacaoProcessoSeletivo notificacao;
	
	/** Lista dos e-mails dos destinat�rios. */
	private Map<String, String> destinatarios;

	public NotificacaoProcessoSeletivo getNotificacao() {
		return notificacao;
	}

	public void setNotificacao(NotificacaoProcessoSeletivo notificacao) {
		this.notificacao = notificacao;
	}

	public Map<String, String> getDestinatarios() {
		return destinatarios;
	}

	public void setDestinatarios(Map<String, String> destinatarios) {
		this.destinatarios = destinatarios;
	}

}
