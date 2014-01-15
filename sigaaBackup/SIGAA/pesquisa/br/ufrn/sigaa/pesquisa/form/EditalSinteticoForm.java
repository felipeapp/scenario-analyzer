/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/04/2007
 *
 */
package br.ufrn.sigaa.pesquisa.form;

import br.ufrn.arq.web.struts.AbstractForm;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Formulário para o relatório sintético de um edital
 *
 * @author Gleydson
 *
 */
public class EditalSinteticoForm extends AbstractForm {

	private int idEdital;

	private Unidade centro;

	private boolean agruparPorDepartamento;

	public EditalSinteticoForm() {
		centro = new Unidade();
	}

	public int getIdEdital() {
		return idEdital;
	}

	public void setIdEdital(int idEdital) {
		this.idEdital = idEdital;
	}

	public Unidade getCentro() {
		return centro;
	}

	public void setCentro(Unidade centro) {
		this.centro = centro;
	}

	public boolean isAgruparPorDepartamento() {
		return agruparPorDepartamento;
	}

	public void setAgruparPorDepartamento(boolean agruparPorDepartamento) {
		this.agruparPorDepartamento = agruparPorDepartamento;
	}



}
