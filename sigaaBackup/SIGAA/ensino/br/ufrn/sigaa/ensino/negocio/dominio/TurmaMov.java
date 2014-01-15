/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
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
 * Movimento para manipulação de turmas
 *
 * @author Gleydson
 *
 */
public class TurmaMov extends MovimentoCadastro {

	/** Turma a cadastrar. */
	private Turma turma;

	/** Registra as alterações da turma de lato sensu. */
	private RegistroAlteracaoLato registroAlteracaoLato;

	/** utilizado apenas quando a solicitação não é de ensino individual.
	 * Quando é turma de ensino individual existe necessariamente apenas uma solicitação
	 * de turma sendo utilizado o objeto abaixo: solicitacaoEnsinoIndividual */
	private Collection<SolicitacaoTurma> solicitacoes;

	/** quando a turma é criada a partir de uma solicitação de ensino individual não é possível ter mais de uma solicitação de turma
	 *	neste caso este objeto é utilizado quando é uma turma de ensino individual e não a coleção solicitações. */
	private SolicitacaoTurma solicitacaoEnsinoIndividualOuFerias;

	/** Indica se a movimentação é de consolidação de turma individual. */
	private boolean consolidacaoIndividual;

	/** Objeto que guarda as informações das configurações da turma selecionada. */
	private ConfiguracoesAva config;
	
	/** Pólos para os quais serão criadas as turmas. */ 
	private Integer[] polos;

	/** Método de avaliação da turma. */
	private Integer metodoAvaliacao;

	/** Calendário acadêmico utilizado na criação da turma. */
	private CalendarioAcademico calendario;

	/** Indica se a turma foi criada para rematrícula. */
	private boolean rematricula;

	/** Indica se a solicitação de turma foi atendida parcialmente. */ 
	private boolean parcial;
	
	/** Coleção de {@link NotaUnidade} iniciais da turma. */
	private Collection<NotaUnidade> notasIniciaisTurma;
	
	/** Coleção de retificações de matrículas da turma. */
	private Collection<RetificacaoMatricula> retificacoesReconsolidacao;
	
	/** Registro de alteração da turma. */
	private AlteracaoTurma alteracaoTurma;
	
	/** Quantidade de subturmas a criar automaticamente. */ 
	private Integer quantidadeSubturmas;
	
	/** Contrutor padrão. */
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

	/** Indica se a movimentação é de consolidação de turma individual.  
	 * @return the consolidacaoIndividual
	 */
	public boolean isConsolidacaoIndividual() {
		return consolidacaoIndividual;
	}

	/** Seta se a movimentação é de consolidação de turma individual. 
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
