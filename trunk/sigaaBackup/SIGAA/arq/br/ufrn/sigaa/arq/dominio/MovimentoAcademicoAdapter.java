/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 19/09/2007 
 */
package br.ufrn.sigaa.arq.dominio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.dominio.CalendarioAcademico;

/**
 * Movimento específico para sistema acadêmico
 * @author Andre Dantas
 *
 */
public class MovimentoAcademicoAdapter extends AbstractMovimentoAdapter {

	private CalendarioAcademico calendarioAcademicoAtual;

	public CalendarioAcademico getCalendarioAcademicoAtual() {
		return calendarioAcademicoAtual;
	}

	public void setCalendarioAcademicoAtual(CalendarioAcademico calendarioAcademicoAtual) {
		this.calendarioAcademicoAtual = calendarioAcademicoAtual;
	}

}
