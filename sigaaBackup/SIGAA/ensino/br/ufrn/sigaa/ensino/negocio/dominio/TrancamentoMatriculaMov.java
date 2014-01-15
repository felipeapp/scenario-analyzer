/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on May 29, 2007
 *
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import java.util.Collection;

import br.ufrn.sigaa.arq.dominio.MovimentoAcademicoAdapter;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoTrancamentoMatricula;

/**
 * Movimento para realizar a solicitação de trancamento de matricula e o atendimento das mesmas
 *
 * @author Victor Hugo
 *
 */
@SuppressWarnings("serial")
public class TrancamentoMatriculaMov extends MovimentoAcademicoAdapter {

	private DiscenteAdapter discente;

	private Collection<SolicitacaoTrancamentoMatricula> solicitacoes;
	
	private boolean aDistancia;

	/**
	 * solicitação de trancamento, utilizado no cancelamento da solicitação de trancamento
	 */
	private SolicitacaoTrancamentoMatricula solicitacao;

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public Collection<SolicitacaoTrancamentoMatricula> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(
			Collection<SolicitacaoTrancamentoMatricula> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public SolicitacaoTrancamentoMatricula getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(SolicitacaoTrancamentoMatricula solicitacao) {
		this.solicitacao = solicitacao;
	}

	public boolean isADistancia() {
		return aDistancia;
	}
	
	public void setADistancia(boolean aDistancia) {
		this.aDistancia = aDistancia;
	}


}
