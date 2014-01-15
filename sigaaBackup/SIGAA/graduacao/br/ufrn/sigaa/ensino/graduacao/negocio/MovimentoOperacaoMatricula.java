/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoTrancamentoMatricula;

/**
 * Movimento para realizar trancamento de matriculas de gradua��o
 *
 * @author ricardo
 * @author Andre Dantas
 *
 */
public class MovimentoOperacaoMatricula extends AbstractMovimentoAdapter {

	/**
	 * Atributo usado no caso da opera��o ser realizada sobre somente uma matr�cula
	 */
	private MatriculaComponente matricula;

	/**
	 * Atributo usado no caso da opera��o ser realizada sobre um conjunto de matr�culas
	 */
	private Collection<MatriculaComponente> matriculas;

	/**
	 * Nova situa��o da(s) matr�cula(s) a serem atualizadas
	 */
	private SituacaoMatricula novaSituacao;

	/**
	 * diz se a opera��o esta sendo invocada automaticamente ou se � um usu�rio que esta invocando.
	 * se for invocada automaticamente n�o � pra executar o checkRole pois n�o ter� nenhum usu�rio logado.
	 */
	private boolean automatico = false;

	/**
	 * se a altera��o da matricula tiver sendo invocada automaticamente para trancar as solicita��es pendentes
	 * esta cole��o dever� estar populada com as solicita��es que ser�o trancadas.
	 * Pois precisa da referencia da solicita��o para executar o movimento
	 */
	private Collection<SolicitacaoTrancamentoMatricula> solicitacoesTrancamento;

	/**
	 * este atributo indica o atendimento  da solicita��o � a dist�ncia.
	 */
	private boolean aDistancia;

	/** este atributo indica se a altera��o da matricula vem da opera��o de cancelar as matriculas
	 * realizadas automaticamente a partir da remo��o de turma de ensino individual.
	 * Na remo��o de turmas de ensino individual as matriculas daquela turma s�o canceladas automaticamente.
	 * Este caso de altera��o de status de matricula p chefe  ou secret�rio de departamento pode realizar */
	private boolean cancelamentoMatriculasEnsinoIndividual;
	
	/**
	 * este atributo indica se a opera��o � a o trancamento de matricula a posteriori pelo discente
	 */
	private boolean trancamentoProgramaPosteriori;

	public SituacaoMatricula getNovaSituacao() {
		return novaSituacao;
	}

	public void setNovaSituacao(SituacaoMatricula novaSituacao) {
		this.novaSituacao = novaSituacao;
	}

	public Collection<MatriculaComponente> getMatriculas() {
		return matriculas;
	}

	public void setMatriculas(Collection<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

	public MovimentoOperacaoMatricula() {

	}

	public MatriculaComponente getMatricula() {
		return matricula;
	}

	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
	}

	public boolean isAutomatico() {
		return automatico;
	}

	public void setAutomatico(boolean automatico) {
		this.automatico = automatico;
	}

	public Collection<SolicitacaoTrancamentoMatricula> getSolicitacoesTrancamento() {
		return solicitacoesTrancamento;
	}

	public void setSolicitacoesTrancamento(
			Collection<SolicitacaoTrancamentoMatricula> solicitacoesTrancamento) {
		this.solicitacoesTrancamento = solicitacoesTrancamento;
	}

	public boolean isADistancia() {
		return aDistancia;
	}

	public void setADistancia(boolean aDistancia) {
		this.aDistancia = aDistancia;
	}

	public boolean isCancelamentoMatriculasEnsinoIndividual() {
		return cancelamentoMatriculasEnsinoIndividual;
	}

	public void setCancelamentoMatriculasEnsinoIndividual(
			boolean cancelamentoMatriculasEnsinoIndividual) {
		this.cancelamentoMatriculasEnsinoIndividual = cancelamentoMatriculasEnsinoIndividual;
	}

	public boolean isTrancamentoProgramaPosteriori() {
		return trancamentoProgramaPosteriori;
	}

	public void setTrancamentoProgramaPosteriori(
			boolean trancamentoProgramaPosteriori) {
		this.trancamentoProgramaPosteriori = trancamentoProgramaPosteriori;
	}

}
