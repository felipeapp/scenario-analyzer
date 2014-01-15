/* 
 * Superintendência de Informática - Diretoria de Sistemas
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
 * Movimento usado para efetuar transferência de alunos entre turmas
 *
 * @author Leonardo
 *
 */
public class MovimentoTransferenciaTurmas extends AbstractMovimentoAdapter {

	/** Valores da quantidade de matrículas de discentes para transferências automáticas*/ 
	private int qtdMatriculas;
	/** Valores da quantidade de solicitações de matrícula de discentes para transferências automáticas*/
	private int qtdSolicitacoes;
	/** Coleção de matrículas de discentes na série.*/
	private Collection<MatriculaComponente> matriculas;
	/** Coleção de solicitações de matrículas de discentes na série.*/
	private Collection<SolicitacaoMatricula> solicitacoes;
	/** Turma de Origem da transferência.*/
	private Turma turmaOrigem;
	/** Turma de Destino da transferência.*/
	private Turma turmaDestino;
	/** Atributo utilizada para verificar se a transferência é feita por apenas um discente.*/
	private boolean transfTurmasByDiscente = false;
	/** Atributo utilizada para verificar se o usuário é administrador do sistema acadêmico.*/
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