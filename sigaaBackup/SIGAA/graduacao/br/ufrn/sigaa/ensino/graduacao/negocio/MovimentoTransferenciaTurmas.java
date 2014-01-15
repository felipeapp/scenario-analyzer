/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.Collection;
import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.TurmaOrigemTurmaDestinos;

/**
 * Movimento usado para efetuar transfer�ncia de alunos entre turmas
 *
 * @author Leonardo
 *
 */
public class MovimentoTransferenciaTurmas extends AbstractMovimentoAdapter {

	/** Valores da quantidade de matr�culas de discentes para transfer�ncias autom�ticas*/ 
	private int qtdMatriculas;
	/** Valores da quantidade de solicita��es de matr�cula de discentes para transfer�ncias autom�ticas*/
	private int qtdSolicitacoes;
	/** Cole��o de matr�culas de discentes na s�rie.*/
	private Collection<MatriculaComponente> matriculas;
	/** Cole��o de solicita��es de matr�culas de discentes na s�rie.*/
	private Collection<SolicitacaoMatricula> solicitacoes;
	/** Turma de Origem da transfer�ncia.*/
	private Turma turmaOrigem;
	/** Turma de Destino da transfer�ncia.*/
	private Turma turmaDestino;
	/** Atributo utilizada para verificar se a transfer�ncia � feita por apenas um discente.*/
	private boolean transfTurmasByDiscente = false;
	/** Atributo utilizada para verificar se o usu�rio � administrador do sistema acad�mico.*/
	private boolean administrador = false;
	/** Lista de Turmas de Destino relacionadas aos componentes das turmas de origem.*/
	private List<TurmaOrigemTurmaDestinos> listTurmaOrigemDestinos;

	/** Construtor. */
	public MovimentoTransferenciaTurmas(){

	}
	
	/** Getters and Setters*/
	
	public int getQtdMatriculas() {
		return qtdMatriculas;
	}
	public void setQtdMatriculas(int qtdMatriculas) {
		this.qtdMatriculas = qtdMatriculas;
	}
	public Collection<MatriculaComponente> getMatriculas() {
		return matriculas;
	}
	public void setMatriculas(Collection<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}
	public Turma getTurmaDestino() {
		return turmaDestino;
	}
	public void setTurmaDestino(Turma turmaDestino) {
		this.turmaDestino = turmaDestino;
	}
	public Turma getTurmaOrigem() {
		return turmaOrigem;
	}
	public void setTurmaOrigem(Turma turmaOrigem) {
		this.turmaOrigem = turmaOrigem;
	}
	public int getQtdSolicitacoes() {
		return this.qtdSolicitacoes;
	}
	public void setQtdSolicitacoes(int qtdSolicitacoes) {
		this.qtdSolicitacoes = qtdSolicitacoes;
	}
	public Collection<SolicitacaoMatricula> getSolicitacoes() {
		return this.solicitacoes;
	}
	public void setSolicitacoes(Collection<SolicitacaoMatricula> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}
	public boolean isTransfTurmasByDiscente() {
		return transfTurmasByDiscente;
	}
	public void setTransfTurmasByDiscente(boolean transfTurmasByDiscente) {
		this.transfTurmasByDiscente = transfTurmasByDiscente;
	}
	public boolean isAdministrador() {
		return administrador;
	}

	public void setAdministrador(boolean administrador) {
		this.administrador = administrador;
	}

	public List<TurmaOrigemTurmaDestinos> getListTurmaOrigemDestinos() {
		return listTurmaOrigemDestinos;
	}
	public void setListTurmaOrigemDestinos(
			List<TurmaOrigemTurmaDestinos> listTurmaOrigemDestinos) {
		this.listTurmaOrigemDestinos = listTurmaOrigemDestinos;
	}
}