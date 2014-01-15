/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.Collection;
import java.util.List;

import br.ufrn.sigaa.arq.dominio.MovimentoAcademico;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ead.dominio.HorarioTutoria;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.OrientacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;

/**
 * movimento para a operação de solicitação de matrícula. encapsula uma lista de
 * solicitações a serem analisadas
 *
 * @author Andre M Dantas
 *
 */
public class MovimentoSolicitacaoMatricula extends MovimentoAcademico {

	public static final int CADASTRAR = 1;

	public static final int SUBMETER_NOVAS = 2;

	public static final int SUBMETER_CADASTRADAS = 3;

	public static final int SUBMETER_JA_ORIENTADAS = 4;

	private Collection<SolicitacaoMatricula> solicitacoes;

	private DiscenteAdapter discente;

	private Collection<Turma> turmas;

	private Collection<ComponenteCurricular> atividades;

	private List<HorarioTutoria> horariosTutoria;

	private OrientacaoMatricula orientacao;

	private boolean rematricula = false;

	private Collection<MatriculaComponente> matriculas;

	private CalendarioAcademico calendarioParaMatricula;
	
	private Integer numSolicitacao = null;
	
	public Collection<ComponenteCurricular> getAtividades() {
		return this.atividades;
	}

	public void setAtividades(Collection<ComponenteCurricular> atividades) {
		this.atividades = atividades;
	}

	public List<HorarioTutoria> getHorariosTutoria() {
		return horariosTutoria;
	}

	public void setHorariosTutoria(List<HorarioTutoria> horariosTutoria) {
		this.horariosTutoria = horariosTutoria;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public Collection<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	public Collection<SolicitacaoMatricula> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(Collection<SolicitacaoMatricula> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public OrientacaoMatricula getOrientacao() {
		return this.orientacao;
	}

	public void setOrientacao(OrientacaoMatricula orientacao) {
		this.orientacao = orientacao;
	}

	public boolean isRematricula() {
		return this.rematricula;
	}

	public void setRematricula(boolean rematricula) {
		this.rematricula = rematricula;
	}

	public Collection<MatriculaComponente> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(Collection<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

	public CalendarioAcademico getCalendarioParaMatricula() {
		return calendarioParaMatricula;
	}

	public void setCalendarioParaMatricula(CalendarioAcademico calendarioParaMatricula) {
		this.calendarioParaMatricula = calendarioParaMatricula;
	}

	public Integer getNumSolicitacao() {
		return numSolicitacao;
	}

	public void setNumSolicitacao(Integer numSolicitacao) {
		this.numSolicitacao = numSolicitacao;
	}

}
