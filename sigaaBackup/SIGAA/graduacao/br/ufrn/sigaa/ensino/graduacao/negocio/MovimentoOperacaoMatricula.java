/* 
 * Superintendência de Informática - Diretoria de Sistemas
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
 * Movimento para realizar trancamento de matriculas de graduação
 *
 * @author ricardo
 * @author Andre Dantas
 *
 */
public class MovimentoOperacaoMatricula extends AbstractMovimentoAdapter {

	/**
	 * Atributo usado no caso da operação ser realizada sobre somente uma matrícula
	 */
	private MatriculaComponente matricula;

	/**
	 * Atributo usado no caso da operação ser realizada sobre um conjunto de matrículas
	 */
	private Collection<MatriculaComponente> matriculas;

	/**
	 * Nova situação da(s) matrícula(s) a serem atualizadas
	 */
	private SituacaoMatricula novaSituacao;

	/**
	 * diz se a operação esta sendo invocada automaticamente ou se é um usuário que esta invocando.
	 * se for invocada automaticamente não é pra executar o checkRole pois não terá nenhum usuário logado.
	 */
	private boolean automatico = false;

	/**
	 * se a alteração da matricula tiver sendo invocada automaticamente para trancar as solicitações pendentes
	 * esta coleção deverá estar populada com as solicitações que serão trancadas.
	 * Pois precisa da referencia da solicitação para executar o movimento
	 */
	private Collection<SolicitacaoTrancamentoMatricula> solicitacoesTrancamento;

	/**
	 * este atributo indica o atendimento  da solicitação é a distância.
	 */
	private boolean aDistancia;

	/** este atributo indica se a alteração da matricula vem da operação de cancelar as matriculas
	 * realizadas automaticamente a partir da remoção de turma de ensino individual.
	 * Na remoção de turmas de ensino individual as matriculas daquela turma são canceladas automaticamente.
	 * Este caso de alteração de status de matricula p chefe  ou secretário de departamento pode realizar */
	private boolean cancelamentoMatriculasEnsinoIndividual;
	
	/**
	 * este atributo indica se a operação é a o trancamento de matricula a posteriori pelo discente
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
