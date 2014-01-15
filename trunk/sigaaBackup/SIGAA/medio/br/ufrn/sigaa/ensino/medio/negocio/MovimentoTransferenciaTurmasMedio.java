/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Movimento usado para efetuar transferência de alunos entre turmas de ensino médio.
 *
 * @author Rafael Gomes
 *
 */
public class MovimentoTransferenciaTurmasMedio extends AbstractMovimentoAdapter {

	/** Valores da quantidade de matrículas de discentes para transferências automáticas*/ 
	private int qtdMatriculas;
	/** Valores da quantidade de solicitações de matrícula de discentes para transferências automáticas*/
	private int qtdSolicitacoes;
	/** Coleção de matrículas de discentes na série.*/
	private Collection<MatriculaDiscenteSerie> matriculas;
	/** Coleção de solicitações de matrículas de discentes na série.*/
	private Collection<SolicitacaoMatricula> solicitacoes;
	/** Turma de Origem da transferência.*/
	private TurmaSerie turmaSerieOrigem;
	/** Turma de Destino da transferência.*/
	private TurmaSerie turmaSerieDestino;
	/** Lista de Turmas de Destino relacionadas aos componentes das turmas de origem.*/
	private List<TurmaOrigemTurmaDestinos> listTurmaOrigemDestinos;

	/** Construtor. */
	public MovimentoTransferenciaTurmasMedio(){
	}

	/** Getters and Setters*/
	/** Valores da quantidade de matrículas de discentes para transferências automáticas*/ 
	public int getQtdMatriculas() {
		return qtdMatriculas;
	}
	/** Valores da quantidade de matrículas de discentes para transferências automáticas*/ 
	public void setQtdMatriculas(int qtdMatriculas) {
		this.qtdMatriculas = qtdMatriculas;
	}
	/** Valores da quantidade de solicitações de matrícula de discentes para transferências automáticas*/
	public int getQtdSolicitacoes() {
		return this.qtdSolicitacoes;
	}
	/** Valores da quantidade de solicitações de matrícula de discentes para transferências automáticas*/
	public void setQtdSolicitacoes(int qtdSolicitacoes) {
		this.qtdSolicitacoes = qtdSolicitacoes;
	}
	/** Coleção de solicitações de matrículas de discentes na série.*/
	public Collection<SolicitacaoMatricula> getSolicitacoes() {
		return this.solicitacoes;
	}
	/** Coleção de solicitações de matrículas de discentes na série.*/
	public void setSolicitacoes(Collection<SolicitacaoMatricula> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}
	/** Coleção de matrículas de discentes na série.*/
	public Collection<MatriculaDiscenteSerie> getMatriculas() {
		return matriculas;
	}
	/** Coleção de matrículas de discentes na série.*/
	public void setMatriculas(Collection<MatriculaDiscenteSerie> matriculas) {
		this.matriculas = matriculas;
	}
	/** Turma de Origem da transferência.*/
	public TurmaSerie getTurmaSerieOrigem() {
		return turmaSerieOrigem;
	}
	/** Turma de Origem da transferência.*/
	public void setTurmaSerieOrigem(TurmaSerie turmaSerieOrigem) {
		this.turmaSerieOrigem = turmaSerieOrigem;
	}
	/** Turma de Destino da transferência.*/
	public TurmaSerie getTurmaSerieDestino() {
		return turmaSerieDestino;
	}
	/** Turma de Destino da transferência.*/
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