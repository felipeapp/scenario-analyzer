/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 11/02/2008 
 *
 */
package br.ufrn.sigaa.processamento.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.processamento.dominio.ModoProcessamentoMatricula;

/**
 * Movimento para ser utilizado no pré-processamento
 * da matrícula.
 * 
 * @author David Pereira
 *
 */
public class PreProcessamentoMov extends AbstractMovimentoAdapter {

	/** Discente cuja matrícula está sendo processada */
	private int dg;
	
	/** Ano do processamento */
	private int ano;
	
	/** Período do processamento */
	private int periodo;
	
	/** Modo de processamento de matrícula (se é de graduação, de férias, etc.) */
	private ModoProcessamentoMatricula modo;
	
	/** Solicitação de matrícula que será transformada em matrícula em espera */
	private SolicitacaoMatricula sm;
	
	/** Se o processamento é de matrícula ou rematrícula */
	private boolean rematricula;

	public int getDiscente() {
		return dg;
	}

	public void setDiscente(int dg) {
		this.dg = dg;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public ModoProcessamentoMatricula getModo() {
		return modo;
	}

	public void setModo(ModoProcessamentoMatricula modo) {
		this.modo = modo;
	}

	public SolicitacaoMatricula getSolicitacao() {
		return sm;
	}

	public void setSolicitacao(SolicitacaoMatricula sm) {
		this.sm = sm;
	}

	public boolean isRematricula() {
		return rematricula;
	}

	public void setRematricula(boolean rematricula) {
		this.rematricula = rematricula;
	}
	
}
