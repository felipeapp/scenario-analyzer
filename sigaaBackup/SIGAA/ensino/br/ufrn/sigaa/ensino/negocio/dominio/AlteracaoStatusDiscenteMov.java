/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jan 30, 2008
 *
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Movimento responsável pela operação de alterar status de discente
 * @author Victor Hugo
 */
public class AlteracaoStatusDiscenteMov extends AbstractMovimentoAdapter {

	/**
	 * o discente cujo status será alterado
	 */
	private Discente discente;

	/** o novo status do discente */
	private StatusDiscente status;

	/** a justificativa da alteração do status */
	private String observacao;

	public Discente getDiscente() {
		return discente;
	}

	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	public StatusDiscente getStatus() {
		return status;
	}

	public void setStatus(StatusDiscente status) {
		this.status = status;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

}
