package br.ufrn.sigaa.apedagogica.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.apedagogica.dominio.AtividadeAtualizacaoPedagogica;
import br.ufrn.sigaa.apedagogica.dominio.ParticipanteAtividadeAtualizacaoPedagogica;

/**
 * Classe que movimenta os dados necess�rios par inscri��o para participa��o das atividades
 * de atualiza��o pedag�gica, como tamb�m para altera��o da situa��o.
 * @author M�rio Rizzi
 *
 */
public class MovimentoInscricaoAtividadeAP extends MovimentoCadastro {
	
	/** Atributo que define o participante */
	private ParticipanteAtividadeAtualizacaoPedagogica participante;
	/** Atributo que define as atividades selecionadas */
	private Collection<AtividadeAtualizacaoPedagogica> atividadesSelecionadas;
	/** 
	  Atributo que define os participantes com a situa��o alterada
	  Utilizado exclusivamente por {@link br.ufrn.sigaa.apedagogica.jsf.GeracaoListaPresencaAPMBean#cadastrar()} 
	  */
	private Collection<ParticipanteAtividadeAtualizacaoPedagogica> participantesSituacaoAlterada;
	
	/** Atributo que define o participante */
	private boolean gestorPAP;

	public Collection<AtividadeAtualizacaoPedagogica> getAtividadesSelecionadas() {
		return atividadesSelecionadas;
	}

	public void setAtividadesSelecionadas(
			Collection<AtividadeAtualizacaoPedagogica> atividadesSelecionadas) {
		this.atividadesSelecionadas = atividadesSelecionadas;
	}

	public ParticipanteAtividadeAtualizacaoPedagogica getParticipante() {
		return participante;
	}

	public void setParticipante(ParticipanteAtividadeAtualizacaoPedagogica participante) {
		this.participante = participante;
	}

	public Collection<ParticipanteAtividadeAtualizacaoPedagogica> getParticipantesSituacaoAlterada() {
		return participantesSituacaoAlterada;
	}

	public void setParticipantesSituacaoAlterada(
			Collection<ParticipanteAtividadeAtualizacaoPedagogica> participantesSituacaoAlterada) {
		this.participantesSituacaoAlterada = participantesSituacaoAlterada;
	}

	public boolean isGestorPAP() {
		return gestorPAP;
	}

	public void setGestorPAP(boolean gestorPAP) {
		this.gestorPAP = gestorPAP;
	}

}
