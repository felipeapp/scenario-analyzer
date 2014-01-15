/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 13/07/2011
 * Autor: Rafael Gomes
 */
package br.ufrn.sigaa.ensino.medio.negocio;

import java.util.Collection;
import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.TurmaOrigemTurmaDestinos;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;

/**
 * Movimento usado para efetuar transfer�ncia de alunos entre turmas de ensino m�dio.
 *
 * @author Rafael Gomes
 *
 */
public class MovimentoTransferenciaTurmasMedio extends AbstractMovimentoAdapter {

	/** Valores da quantidade de matr�culas de discentes para transfer�ncias autom�ticas*/ 
	private int qtdMatriculas;
	/** Valores da quantidade de solicita��es de matr�cula de discentes para transfer�ncias autom�ticas*/
	private int qtdSolicitacoes;
	/** Cole��o de matr�culas de discentes na s�rie.*/
	private Collection<MatriculaDiscenteSerie> matriculas;
	/** Cole��o de solicita��es de matr�culas de discentes na s�rie.*/
	private Collection<SolicitacaoMatricula> solicitacoes;
	/** Turma de Origem da transfer�ncia.*/
	private TurmaSerie turmaSerieOrigem;
	/** Turma de Destino da transfer�ncia.*/
	private TurmaSerie turmaSerieDestino;
	/** Lista de Turmas de Destino relacionadas aos componentes das turmas de origem.*/
	private List<TurmaOrigemTurmaDestinos> listTurmaOrigemDestinos;

	/** Construtor. */
	public MovimentoTransferenciaTurmasMedio(){
	}

	/** Getters and Setters*/
	/** Valores da quantidade de matr�culas de discentes para transfer�ncias autom�ticas*/ 
	public int getQtdMatriculas() {
		return qtdMatriculas;
	}
	/** Valores da quantidade de matr�culas de discentes para transfer�ncias autom�ticas*/ 
	public void setQtdMatriculas(int qtdMatriculas) {
		this.qtdMatriculas = qtdMatriculas;
	}
	/** Valores da quantidade de solicita��es de matr�cula de discentes para transfer�ncias autom�ticas*/
	public int getQtdSolicitacoes() {
		return this.qtdSolicitacoes;
	}
	/** Valores da quantidade de solicita��es de matr�cula de discentes para transfer�ncias autom�ticas*/
	public void setQtdSolicitacoes(int qtdSolicitacoes) {
		this.qtdSolicitacoes = qtdSolicitacoes;
	}
	/** Cole��o de solicita��es de matr�culas de discentes na s�rie.*/
	public Collection<SolicitacaoMatricula> getSolicitacoes() {
		return this.solicitacoes;
	}
	/** Cole��o de solicita��es de matr�culas de discentes na s�rie.*/
	public void setSolicitacoes(Collection<SolicitacaoMatricula> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}
	/** Cole��o de matr�culas de discentes na s�rie.*/
	public Collection<MatriculaDiscenteSerie> getMatriculas() {
		return matriculas;
	}
	/** Cole��o de matr�culas de discentes na s�rie.*/
	public void setMatriculas(Collection<MatriculaDiscenteSerie> matriculas) {
		this.matriculas = matriculas;
	}
	/** Turma de Origem da transfer�ncia.*/
	public TurmaSerie getTurmaSerieOrigem() {
		return turmaSerieOrigem;
	}
	/** Turma de Origem da transfer�ncia.*/
	public void setTurmaSerieOrigem(TurmaSerie turmaSerieOrigem) {
		this.turmaSerieOrigem = turmaSerieOrigem;
	}
	/** Turma de Destino da transfer�ncia.*/
	public TurmaSerie getTurmaSerieDestino() {
		return turmaSerieDestino;
	}
	/** Turma de Destino da transfer�ncia.*/
	public void setTurmaSerieDestino(TurmaSerie turmaSerieDestino) {
		this.turmaSerieDestino = turmaSerieDestino;
	}
	/** Lista de Turmas de Destino relacionadas aos componentes das turmas de origem.*/
	public List<TurmaOrigemTurmaDestinos> getListTurmaOrigemDestinos() {
		return listTurmaOrigemDestinos;
	}
	/** Lista de Turmas de Destino relacionadas aos componentes das turmas de origem.*/
	public void setListTurmaOrigemDestinos(
			List<TurmaOrigemTurmaDestinos> listTurmaOrigemDestinos) {
		this.listTurmaOrigemDestinos = listTurmaOrigemDestinos;
	}
}