/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/12/2008
 *
 */
package br.ufrn.sigaa.espacofisico.jsf;

import java.util.Date;

import org.apache.myfaces.custom.schedule.model.DefaultScheduleEntry;

import br.ufrn.sigaa.espacofisico.dominio.ReservaHorario;

public class ReservaHorarioAdapter extends DefaultScheduleEntry {

	private ReservaHorario horario;
	
	public ReservaHorarioAdapter(ReservaHorario horario) {
		this.horario = horario;
	}
	
	@Override
	public String getDescription() {
		return horario.getDescricao();
	}

	@Override
	public Date getEndTime() {
		return horario.getFim();
	}

	@Override
	public String getId() {
		return String.valueOf(horario.getId());
	}
	
	@Override
	public Date getStartTime() {
		return horario.getInicio();
	}

	@Override
	public String getTitle() {
		return horario.getTitulo();
	}
	
	public ReservaHorario getHorario() {
		return horario;
	}

	public void setHorario(ReservaHorario horario) {
		this.horario = horario;
	}

}
