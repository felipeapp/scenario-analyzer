/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 *
 * Created on 26/09/2006
 *
 */
package br.ufrn.sigaa.ensino.form;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
import br.ufrn.sigaa.ensino.tecnico.dominio.ModuloCurricular;
import br.ufrn.sigaa.ensino.tecnico.dominio.TurmaEntradaTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Form para cadastramento de matrículas
 *
 * @author David Ricardo
 *
 */
public class MatriculaTecnicoForm extends SigaaForm<MatriculaComponente> {

	private CursoTecnico cursoTecnico;

	private Discente discente;

	private ModuloCurricular modulo;

	private TurmaEntradaTecnico turma;

	private String type;

	private List<Turma> turmas;

	private int id;

	private boolean disciplina;

	public MatriculaTecnicoForm() {
		this.obj = new MatriculaComponente();
		this.obj.setTurma(new Turma());
		this.obj.getTurma().setDisciplina(new ComponenteCurricular());

		cursoTecnico = new CursoTecnico();
		discente = new Discente();
		turma = new TurmaEntradaTecnico();
		modulo = new ModuloCurricular();
		modulo.setModulo(new Modulo());

		turmas = new ArrayList<Turma>();
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the discente
	 */
	public Discente getDiscente() {
		return discente;
	}

	/**
	 * @param discente the discente to set
	 */
	public void setDiscente(Discente discente) {
		this.discente = discente;
	}

	/**
	 * @return the cursoTecnico
	 */
	public CursoTecnico getCursoTecnico() {
		return cursoTecnico;
	}

	/**
	 * @param cursoTecnico the cursoTecnico to set
	 */
	public void setCursoTecnico(CursoTecnico cursoTecnico) {
		this.cursoTecnico = cursoTecnico;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}


	/**
	 * @return the modulo
	 */
	public ModuloCurricular getModulo() {
		return modulo;
	}

	/**
	 * @param modulo the modulo to set
	 */
	public void setModulo(ModuloCurricular modulo) {
		this.modulo = modulo;
	}

	/**
	 * @return the turma
	 */
	public TurmaEntradaTecnico getTurma() {
		return turma;
	}

	/**
	 * @param turma the turma to set
	 */
	public void setTurma(TurmaEntradaTecnico turma) {
		this.turma = turma;
	}

	/**
	 * @return the turmas
	 */
	public List<Turma> getTurmas() {
		return turmas;
	}

	/**
	 * @param turmas the turmas to set
	 */
	public void setTurmas(List<Turma> turmas) {
		this.turmas = turmas;
	}

	/**
	 * @return the disciplina
	 */
	public boolean isDisciplina() {
		return disciplina;
	}

	/**
	 * @param disciplina the disciplina to set
	 */
	public void setDisciplina(boolean disciplina) {
		this.disciplina = disciplina;
	}

	public void validateDisciplinas(HttpServletRequest req) throws DAOException {
		if (turmas == null || turmas.isEmpty()) {
			addMensagemErro("É necessário escolher ao menos uma disciplina.", req);
		}
	}


}
