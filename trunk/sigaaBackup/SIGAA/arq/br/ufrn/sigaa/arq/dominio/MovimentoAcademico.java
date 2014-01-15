/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 19/09/2007 
 */
package br.ufrn.sigaa.arq.dominio;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.dominio.CalendarioAcademico;

/**
 *
 * Movimento específico para sistema acadêmico
 * @author Andre Dantas
 *
 */
public class MovimentoAcademico extends MovimentoCadastro {

	private CalendarioAcademico calendarioAcademicoAtual;

	private int ano;
	
	private int periodo;
	
	private boolean rematricula;
	
	public CalendarioAcademico getCalendarioAcademicoAtual() {
		return calendarioAcademicoAtual;
	}

	public void setCalendarioAcademicoAtual(CalendarioAcademico calendarioAtual) {
		this.calendarioAcademicoAtual = calendarioAtual;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public boolean isRematricula() {
		return rematricula;
	}

	public void setRematricula(boolean rematricula) {
		this.rematricula = rematricula;
	}

}
