/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jun 24, 2008
 *
 */
package br.ufrn.sigaa.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.AgendaRevalidacaoDiploma;

/**
 * Classe responsável pelo movimento da agenda para revalidação de diplomas 
 * @author Mário Rizzi Rocha
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
