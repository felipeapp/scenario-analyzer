package br.ufrn.sigaa.apedagogica.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.apedagogica.dominio.NotificacaoParticipanteAtividade;
import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;

public class MovimentoNotificarParticipanteAP extends AbstractMovimentoAdapter {


	/** Objeto a ser persistido	 */
	private NotificacaoParticipanteAtividade notificacao;
	
	private Collection<ParticipanteAtividadeAtualizacaoPedagogica> participantes;
	

	public NotificacaoParticipanteAtividade getNotificacao() {
		return notificacao;
	}

	public void setNotificacao(NotificacaoParticipanteAtividade notificacao) {
		this.notificacao = notificacao;
	}

	public Collection<ParticipanteAtividadeAtualizacaoPedagogica> getParticipantes() {
		return participantes;
	}

	public void setParticipantes(
			Collection<ParticipanteAtividadeAtualizacaoPedagogica> participantes) {
		this.participantes = participantes;
	}
	
}
