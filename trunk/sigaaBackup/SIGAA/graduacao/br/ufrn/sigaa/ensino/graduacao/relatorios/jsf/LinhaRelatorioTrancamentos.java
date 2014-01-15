/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * 31/05/2007
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.util.Map;
import java.util.TreeMap;

import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;


/**
 * Classe auxiliar utilizada para gerar relatórios de trancamentos
 * @author leonardo
 *
 */
public class LinhaRelatorioTrancamentos {

	/** Disciplina onde há trancamento. */
	private ComponenteCurricular disciplina;
	
	/** Lista de alunos trancados na disciplina. */
	private Map<String, DiscenteAdapter> discentes = new TreeMap<String, DiscenteAdapter>();
	
	/** Construtor padrão. */
	public LinhaRelatorioTrancamentos(){
	}

	/** Retorna a lista de alunos trancados na disciplina. 
	 * @return
	 */
	public Map<String, DiscenteAdapter> getDiscentes() {
		return discentes;
	}

	/** Seta a lista de alunos trancados na disciplina.
	 * @param discentes
	 */
	public void setDiscentes(Map<String, DiscenteAdapter> discentes) {
		this.discentes = discentes;
	}

	/** Retorna a disciplina onde há trancamento. 
	 * @return
	 */
	public ComponenteCurricular getDisciplina() {
		return disciplina;
	}

	/** Seta a disciplina onde há trancamento.
	 * @param disciplina
	 */
	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}
	
	
}
