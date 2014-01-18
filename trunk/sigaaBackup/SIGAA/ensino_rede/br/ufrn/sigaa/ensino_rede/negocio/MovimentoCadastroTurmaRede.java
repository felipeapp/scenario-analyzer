 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 15/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.negocio;

import java.util.ArrayList;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.DocenteRede;
import br.ufrn.sigaa.ensino_rede.dominio.TurmaRede;

/**
 * Movimento para realização de cadastros de turma no ensino em rede
 * 
 * @author Diego Jácome
 *
 */
public class MovimentoCadastroTurmaRede extends MovimentoCadastro {

	/** Turma que será cadastrada */
	private TurmaRede turma;
	
	/** Discentes que serão matriculados na turma */
	private ArrayList<DiscenteAssociado> discentes;
	
	/** Discentes que serão desmatriculados na turma */
	private ArrayList<DiscenteAssociado> discentesRemovidos;
	
	/** Docentes que serão associados a turma */
	private ArrayList<DocenteRede> docentes;

	/** Docentes que serão desassociados a turma */
	private ArrayList<DocenteRede> docentesRemovidos;
	
	/** Campus em que a turma será cadastrada */
	private CampusIes campus;
	
	public void setTurma(TurmaRede turma) {
		this.turma = turma;
	}

	public TurmaRede getTurma() {
		return turma;
	}

	public void setDiscentes(ArrayList<DiscenteAssociado> discentes) {
		this.discentes = discentes;
	}

	public ArrayList<DiscenteAssociado> getDiscentes() {
		return discentes;
	}

	public void setDocentes(ArrayList<DocenteRede> docentes) {
		this.docentes = docentes;
	}

	public ArrayList<DocenteRede> getDocentes() {
		return docentes;
	}

	public void setDiscentesRemovidos(ArrayList<DiscenteAssociado> discentesRemovidos) {
		this.discentesRemovidos = discentesRemovidos;
	}

	public ArrayList<DiscenteAssociado> getDiscentesRemovidos() {
		return discentesRemovidos;
	}

	public void setDocentesRemovidos(ArrayList<DocenteRede> docentesRemovidos) {
		this.docentesRemovidos = docentesRemovidos;
	}

	public ArrayList<DocenteRede> getDocentesRemovidos() {
		return docentesRemovidos;
	}

	public void setCampus(CampusIes campus) {
		this.campus = campus;
	}

	public CampusIes getCampus() {
		return campus;
	}
	
}
