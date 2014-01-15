/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Jan 30, 2008
 *
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Movimento respons�vel pela opera��o de alterar status de discente
 * @author Victor Hugo
 */
public class AlteracaoStatusDiscenteMov extends AbstractMovimentoAdapter {

	/**
	 * o discente cujo status ser� alterado
	 */
	private Discente discente;

	/** o novo status do discente */
	private StatusDiscente status;

	/** a justificativa da altera��o do status */
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
