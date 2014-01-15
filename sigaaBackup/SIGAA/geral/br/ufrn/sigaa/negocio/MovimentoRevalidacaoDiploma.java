/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jun 24, 2008
 *
 */
package br.ufrn.sigaa.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoRevalidacaoDiploma;

/**
 * Classe responsável pelo movimento das solicitações de revalidação de diploma
 * @author Mário Rizzi Rocha
 */
public class MovimentoRevalidacaoDiploma extends AbstractMovimentoAdapter {
	
	private SolicitacaoRevalidacaoDiploma revalidacaoDiploma;

	public SolicitacaoRevalidacaoDiploma getRevalidacaoDiploma() {
		return revalidacaoDiploma;
	}

	public void setRevalidacaoDiploma(SolicitacaoRevalidacaoDiploma revalidacaoDiploma) {
		this.revalidacaoDiploma = revalidacaoDiploma;
	}

}
