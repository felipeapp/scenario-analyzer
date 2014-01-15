/*
 * Sistema Integrado de Patrim�nio e Administra��o de Contratos
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import java.util.Collection;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.AlteracaoTurma;
import br.ufrn.sigaa.ensino.dominio.NotaUnidade;
import br.ufrn.sigaa.ensino.dominio.RetificacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.latosensu.dominio.RegistroAlteracaoLato;

/**
 * Movimento para manipula��o de turmas
 *
 * @author Gleydson
 *
 */
public class TurmaMov extends MovimentoCadastro {

	/** Turma a cadastrar. */
	private Turma turma;

	/** Registra as altera��es da turma de lato sensu. */
	private RegistroAlteracaoLato registroAlteracaoLato;

	/** utilizado apenas quando a solicita��o n�o � de ensino individual.
	 * Quando � turma de ensino individual existe necessariamente apenas uma solicita��o
	 * de turma sendo utilizado o objeto abaixo: solicitacaoEnsinoIndividual */
	private Collection<SolicitacaoTurma> solicitacoes;

	/** quando a turma � criada a partir de uma solicita��o de ensino individual n�o � poss�vel ter mais de uma solicita��o de turma
	 *	neste caso este objeto � utilizado quando � uma turma de ensino individual e n�o a cole��o solicita��es. */
	private SolicitacaoTurma solicitacaoEnsinoIndividualOuFerias;

	/** Indica se a movimenta��o � de consolida��o de turma individual. */
	private boolean consolidacaoIndividual;

	/** Objeto que guarda as informa��es das configura��es da turma selecionada. */
	private ConfiguracoesAva config;
	
	/** P�los para os quais ser�o criadas as turmas. */ 
	private Integer[] polos;

	/** M�todo de avalia��o da turma. */
	private Integer metodoAvaliacao;

	/** Calend�rio acad�mico utilizado na cria��o da turma. */
	private CalendarioAcademico calendario;

	/** Indica se a turma foi criada para rematr�cula. */
	private boolean rematricula;

	/** Indica se a solicita��o de turma foi atendida parcialmente. */ 
	private boolean parcial;
	
	/** Cole��o de {@link NotaUnidade} iniciais da turma. */
	private Collection<NotaUnidade> notasIniciaisTurma;
	
	/** Cole��o de retifica��es de matr�culas da turma. */
	private Collection<RetificacaoMatricula> retificacoesReconsolidacao;
	
	/** Registro de altera��o da turma. */
	private AlteracaoTurma alteracaoTurma;
	
	/** Quantidade de subturmas a criar automaticamente. */ 
	private Integer quantidadeSubturmas;
	
	/** Contrutor padr�o. */
	public TurmaMov() {

	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub

	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public RegistroAlteracaoLato getRegistroAlteracaoLato() {
		return registroAlteracaoLato;
	}

	public void setRegistroAlteracaoLato(RegistroAlteracaoLato registroAlteracaoLato) {
		this.registroAlteracaoLato = registroAlteracaoLato;
	}

	public Collection<SolicitacaoTurma> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(Collection<SolicitacaoTurma> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	/** Indica se a movimenta��o � de consolida��o de turma individual.  
	 * @return the consolidacaoIndividual
	 */
	public boolean isConsolidacaoIndividual() {
		return consolidacaoIndividual;
	}

	/** Seta se a movimenta��o � de consolida��o de turma individual. 
	 * @param consolidacaoIndividual the consolidacaoIndividual to set
	 */
	public void setConsolidacaoIndividual(boolean consolidacaoIndividual) {
		this.consolidacaoIndividual = consolidacaoIndividual;
	}

	public Integer[] getPolos() {
		return polos;
	}

	public void setPolos(Integer[] polos) {
		this.polos = polos;
	}

	public Integer getMetodoAvaliacao() {
		return metodoAvaliacao;
	}

	public void setMetodoAvaliacao(Integer metodoAvaliacao) {
		this.metodoAvaliacao = metodoAvaliacao;
	}

	public SolicitacaoTurma getSolicitacaoEnsinoIndividualOuFerias() {
		return solicitacaoEnsinoIndividualOuFerias;
	}

	public void setSolicitacaoEnsinoIndividualOuFerias(
			SolicitacaoTurma solicitacaoEnsinoIndividual) {
		this.solicitacaoEnsinoIndividualOuFerias = solicitacaoEnsinoIndividual;
	}

	public CalendarioAcademico getCalendario() {
		return calendario;
	}

	public void setCalendario(CalendarioAcademico calendario) {
		this.calendario = calendario;
	}

	public boolean isRematricula() {
		return rematricula;
	}

	public void setRematricula(boolean rematricula) {
		this.rematricula = rematricula;
	}

	public void setConsolidacaoPartial(boolean parcial) {
		this.parcial = parcial;
	}
	
	public boolean isParcial() {
		return parcial;
	}

	public Collection<NotaUnidade> getNotasIniciaisTurma() {
		return notasIniciaisTurma;
	}

	public void setNotasIniciaisTurma(Collection<NotaUnidade> notasIniciaisTurma) {
		this.notasIniciaisTurma = notasIniciaisTurma;
	}

	public Collection<RetificacaoMatricula> getRetificacoesReconsolidacao() {
		return retificacoesReconsolidacao;
	}

	public void setRetificacoesReconsolidacao(Collection<RetificacaoMatricula> retificacoesReconsolidacao) {
		this.retificacoesReconsolidacao = retificacoesReconsolidacao;
	}

	public AlteracaoTurma getAlteracaoTurma() {
		return alteracaoTurma;
	}

	public void setAlteracaoTurma(AlteracaoTurma alteracaoTurma) {
		this.alteracaoTurma = alteracaoTurma;
	}

	public Integer getQuantidadeSubturmas() {
		return quantidadeSubturmas;
	}

	public void setQuantidadeSubturmas(Integer quantidadeSubturmas) {
		this.quantidadeSubturmas = quantidadeSubturmas;
	}

	public void setConfig(ConfiguracoesAva config) {
		this.config = config;
	}

	public ConfiguracoesAva getConfig() {
		return config;
	}	
	
}
