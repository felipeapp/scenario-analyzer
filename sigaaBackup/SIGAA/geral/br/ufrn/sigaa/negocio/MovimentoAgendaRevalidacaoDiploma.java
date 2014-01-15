/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Jun 24, 2008
 *
 */
package br.ufrn.sigaa.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.AgendaRevalidacaoDiploma;

/**
 * Classe respons�vel pelo movimento da agenda para revalida��o de diplomas 
 * @author M�rio Rizzi Rocha
 */
public class MovimentoAgendaRevalidacaoDiploma extends AbstractMovimentoAdapter {
	
	private AgendaRevalidacaoDiploma agendaRevalidacaoDiploma;

	public AgendaRevalidacaoDiploma getAgendaRevalidacaoDiploma() {
		return agendaRevalidacaoDiploma;
	}

	public void setAgendaRevalidacaoDiploma(AgendaRevalidacaoDiploma agendaRevalidacaoDiploma) {
		this.agendaRevalidacaoDiploma = agendaRevalidacaoDiploma;
	}

}
