/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/12/2008
 *
 */
package br.ufrn.sigaa.espacofisico.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.espacofisico.dominio.Reserva;
import br.ufrn.sigaa.espacofisico.dominio.ReservaHorario;

public class MovimentoReservaEspacoFisico extends AbstractMovimentoAdapter {

	private Reserva reserva;

	private List<ReservaHorario> removerHorarios;

	public Reserva getReserva() {
		return reserva;
	}

	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}

	public List<ReservaHorario> getRemoverHorarios() {
		return removerHorarios;
	}

	public void setRemoverHorarios(List<ReservaHorario> removerHorarios) {
		this.removerHorarios = removerHorarios;
	}

}
