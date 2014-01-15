/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Jun 24, 2008
 *
 */
package br.ufrn.sigaa.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoRevalidacaoDiploma;

/**
 * Classe respons�vel pelo movimento das solicita��es de revalida��o de diploma
 * @author M�rio Rizzi Rocha
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
