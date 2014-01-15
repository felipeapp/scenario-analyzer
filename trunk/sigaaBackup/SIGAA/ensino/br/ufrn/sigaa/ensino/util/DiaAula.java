/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ensino.util;

import java.util.Date;

/**
 * Classe utilizada para listar todos os dias de aula de uma turma
 *
 * @author Gleydson
 *
 */
public class DiaAula {

	private Date data;

	private Date horaInicio;

	private Date horaFim;

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Date getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(Date horaFim) {
		this.horaFim = horaFim;
	}

	public Date getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Date horaInicio) {
		this.horaInicio = horaInicio;
	}

}
