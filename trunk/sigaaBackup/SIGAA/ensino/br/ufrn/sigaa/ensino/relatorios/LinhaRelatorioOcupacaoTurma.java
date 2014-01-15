/*
 * Sistema Integrado de Patrim�nio e Administra��o de Contratos
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.ensino.relatorios;

import java.util.Map;
import java.util.TreeMap;

import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Classe auxiliar para gera��o de relat�rio de ocupa��o de turma.
 * @author leonardo
 *
 */
public class LinhaRelatorioOcupacaoTurma {

	
	private String nomeDisciplina;
	
	private String codigoDisciplina;
	
	private Map<String, Turma> turmas = new TreeMap<String, Turma>();
	
	public LinhaRelatorioOcupacaoTurma(){
	}

	
	public String getCodigoDisciplina() {
		return codigoDisciplina;
	}


	public void setCodigoDisciplina(String codigoDisciplina) {
		this.codigoDisciplina = codigoDisciplina;
	}


	public String getNomeDisciplina() {
		return nomeDisciplina;
	}


	public void setNomeDisciplina(String nomeDisciplina) {
		this.nomeDisciplina = nomeDisciplina;
	}


	public Map<String, Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(Map<String, Turma> turmas) {
		this.turmas = turmas;
	}
	
	
}
