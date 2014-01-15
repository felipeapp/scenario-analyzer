/*
 * Sistema Integrado de Patrim�nio e Administra��o de Contratos
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 07/03/2007
 *
 */
package br.ufrn.rh.dominio;

import java.util.Date;

/**
 * Classe que representa as f�rias de um servidor.
 * 
 * @author Itamir
 *
 */

public class Ferias {

	private int id;
	private Date dataInicio;
	private Date dataFim;

	public Date getDataFim() {
		return dataFim;
	}
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	public Date getDataInicio() {
		return dataInicio;
	}
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

}
