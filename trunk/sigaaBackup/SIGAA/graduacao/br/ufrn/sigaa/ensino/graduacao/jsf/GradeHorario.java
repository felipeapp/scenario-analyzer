/*
 * Superintendência de Informática - UFRN
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * 09/02/2007
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import br.ufrn.sigaa.ensino.dominio.Horario;

/**
 * Classe Auxiliar para gerar uma grade de horários
 *
 * @author Leonardo
 *
 */
public class GradeHorario {

	private Horario horario;
	
	private boolean segunda, terca, quarta, quinta, sexta, sabado, domingo;

	public boolean isDomingo() {
		return domingo;
	}

	public void setDomingo(boolean domingo) {
		this.domingo = domingo;
	}

	public Horario getHorario() {
		return horario;
	}

	public void setHorario(Horario horario) {
		this.horario = horario;
	}

	public boolean isQuarta() {
		return quarta;
	}

	public void setQuarta(boolean quarta) {
		this.quarta = quarta;
	}

	public boolean isQuinta() {
		return quinta;
	}

	public void setQuinta(boolean quinta) {
		this.quinta = quinta;
	}

	public boolean isSabado() {
		return sabado;
	}

	public void setSabado(boolean sabado) {
		this.sabado = sabado;
	}

	public boolean isSegunda() {
		return segunda;
	}

	public void setSegunda(boolean segunda) {
		this.segunda = segunda;
	}

	public boolean isSexta() {
		return sexta;
	}

	public void setSexta(boolean sexta) {
		this.sexta = sexta;
	}

	public boolean isTerca() {
		return terca;
	}

	public void setTerca(boolean terca) {
		this.terca = terca;
	}
	
	public boolean hasDays(){
		return domingo||segunda||terca||quarta||quinta||sexta||sabado;
	}
	
	public boolean hasSameDays(GradeHorario grade){
		return 	this.domingo == grade.isDomingo() &&
				this.segunda == grade.isSegunda() &&
				this.terca   == grade.isTerca()   &&
				this.quarta  == grade.isQuarta()  &&
				this.quinta  == grade.isQuinta()  &&
				this.sexta   == grade.isSexta()   &&
				this.sabado  == grade.isSabado();
	}
}
