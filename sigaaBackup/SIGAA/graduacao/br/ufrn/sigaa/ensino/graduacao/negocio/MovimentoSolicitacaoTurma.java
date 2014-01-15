/*
 * Superintendência de Informática - UFRN
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * 01/02/2007
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;


/**
 * Movimento para realizar solicitações de abertura de turmas
 *
 * @author Leonardo
 *
 */
public class MovimentoSolicitacaoTurma extends AbstractMovimentoAdapter {

	private SolicitacaoTurma solicitacao;
	
	private Collection<ReservaCurso> reservas;
	
	private Collection<SolicitacaoTurma> solicitacoes;
	
	public MovimentoSolicitacaoTurma(){
	}

	public Collection<ReservaCurso> getReservas() {
		return reservas;
	}

	public void setReservas(Collection<ReservaCurso> reservas) {
		this.reservas = reservas;
	}

	public SolicitacaoTurma getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(SolicitacaoTurma solicitacao) {
		this.solicitacao = solicitacao;
	}

	public Collection<SolicitacaoTurma> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(Collection<SolicitacaoTurma> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}
	
	
}
