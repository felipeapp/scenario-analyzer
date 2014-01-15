/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 11/12/2006
 *
 */
package br.ufrn.sigaa.pesquisa.form;

import br.ufrn.sigaa.arq.struts.SigaaForm;

/**
 * Formul�rio utilizado no cadastro dos par�metros do m�dulo de pesquisa
 * 
 * @author Ricardo Wendell
 * @author Leonardo Campos
 */
@SuppressWarnings("unchecked")
public class ParametrosPesquisaForm extends SigaaForm {

	/** Quantidade m�xima que um projeto de pesquisa pode ser renovado */
	private int qtdMaximaRenovacoes;

	/** Toler�ncia em horas para submiss�o de projetos ap�s o fim do prazo do �ltimo edital */
	private int toleranciaSubmissao;

	/** Dura��o m�xima (em meses) para novos projetos de pesquisa */
	private int duracaoMaximaProjetos;

	/** Limite de solicita��o de cotas por projeto */
	private int limiteCotasProjeto;

	/** Limite de solicita��o de cotas por orientador */
	private int limiteCotasOrientador;

	/** Per�odo de submiss�o de relat�rios parciais */
	private String inicioSubmissaoRelatorioParcial;
	private String fimSubmissaoRelatorioParcial;

	/** Per�odo de avalia��o pelos consultores */
	private String inicioAvaliacaoConsultores;
	private String fimAvaliacaoConsultores;

	/** Dia limite para que substitui��o de bolsista passe a valer no m�s corrente */
	private int limiteSubstituicao;
	
	/** Email para recebimento de notifica��o de altera��es de bolsistas */
	private String emailNotificacao;
	
	/** Email para recebimento de notifica��es de inven��o submetidas no SIGAA */
	private String emailNotificacaoInvencao;
	
	/** Permite envio de relat�rios parciais pelos alunos de inicia��o cient�fica? (Sim/N�o) */
	private boolean envioRelatorioParcial;
	
	/** Permite envio de resumos do CIC independentes? (Sim/N�o) */
	private boolean resumoIndependente;
	
	public ParametrosPesquisaForm() {

	}

	/**
	 * @return the duracaoMaximaProjetos
	 */
	public int getDuracaoMaximaProjetos() {
		return duracaoMaximaProjetos;
	}

	/**
	 * @return the limiteCotasOrientador
	 */
	public int getLimiteCotasOrientador() {
		return limiteCotasOrientador;
	}

	/**
	 * @return the limiteCotasProjeto
	 */
	public int getLimiteCotasProjeto() {
		return limiteCotasProjeto;
	}

	/**
	 * @return the toleranciaSubmissao
	 */
	public int getToleranciaSubmissao() {
		return toleranciaSubmissao;
	}

	/**
	 * @param duracaoMaximaProjetos the duracaoMaximaProjetos to set
	 */
	public void setDuracaoMaximaProjetos(int duracaoMaximaProjetos) {
		this.duracaoMaximaProjetos = duracaoMaximaProjetos;
	}

	/**
	 * @param limiteCotasOrientador the limiteCotasOrientador to set
	 */
	public void setLimiteCotasOrientador(int limiteCotasOrientador) {
		this.limiteCotasOrientador = limiteCotasOrientador;
	}

	/**
	 * @param limiteCotasProjeto the limiteCotasProjeto to set
	 */
	public void setLimiteCotasProjeto(int limiteCotasProjeto) {
		this.limiteCotasProjeto = limiteCotasProjeto;
	}

	/**
	 * @param toleranciaSubmissao the toleranciaSubmissao to set
	 */
	public void setToleranciaSubmissao(int toleranciaSubmissao) {
		this.toleranciaSubmissao = toleranciaSubmissao;
	}

	public String getFimSubmissaoRelatorioParcial() {
		return fimSubmissaoRelatorioParcial;
	}

	public String getInicioSubmissaoRelatorioParcial() {
		return inicioSubmissaoRelatorioParcial;
	}

	public void setFimSubmissaoRelatorioParcial(String fimSubmissaoRelatorioParcial) {
		this.fimSubmissaoRelatorioParcial = fimSubmissaoRelatorioParcial;
	}

	public void setInicioSubmissaoRelatorioParcial(
			String inicioSubmissaoRelatorioParcial) {
		this.inicioSubmissaoRelatorioParcial = inicioSubmissaoRelatorioParcial;
	}

	public int getQtdMaximaRenovacoes() {
		return qtdMaximaRenovacoes;
	}

	public void setQtdMaximaRenovacoes(int qtdMaximaRenovacoes) {
		this.qtdMaximaRenovacoes = qtdMaximaRenovacoes;
	}

	public String getFimAvaliacaoConsultores() {
		return fimAvaliacaoConsultores;
	}

	public void setFimAvaliacaoConsultores(String fimAvaliacaoConsultores) {
		this.fimAvaliacaoConsultores = fimAvaliacaoConsultores;
	}

	public String getInicioAvaliacaoConsultores() {
		return inicioAvaliacaoConsultores;
	}

	public void setInicioAvaliacaoConsultores(String inicioAvaliacaoConsultores) {
		this.inicioAvaliacaoConsultores = inicioAvaliacaoConsultores;
	}

	public int getLimiteSubstituicao() {
		return limiteSubstituicao;
	}

	public void setLimiteSubstituicao(int limiteSubstituicao) {
		this.limiteSubstituicao = limiteSubstituicao;
	}

	public String getEmailNotificacao() {
		return emailNotificacao;
	}

	public void setEmailNotificacao(String emailNotificacao) {
		this.emailNotificacao = emailNotificacao;
	}

	public String getEmailNotificacaoInvencao() {
		return emailNotificacaoInvencao;
	}

	public void setEmailNotificacaoInvencao(String emailNotificacaoInvencao) {
		this.emailNotificacaoInvencao = emailNotificacaoInvencao;
	}

	public boolean isEnvioRelatorioParcial() {
		return envioRelatorioParcial;
	}

	public void setEnvioRelatorioParcial(boolean envioRelatorioParcial) {
		this.envioRelatorioParcial = envioRelatorioParcial;
	}

	public boolean isResumoIndependente() {
		return resumoIndependente;
	}

	public void setResumoIndependente(boolean resumoIndependente) {
		this.resumoIndependente = resumoIndependente;
	}

}
