/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.util.Date;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.pesquisa.dominio.MembroProjetoDiscente;
import br.ufrn.sigaa.pesquisa.dominio.PlanoTrabalho;

/**
 * Entidade responsável pelo movimento de indicação de bolsista
 * 
 * @author Victor Hugo
 */
public class MovimentoIndicarBolsista extends AbstractMovimentoAdapter {

	private PlanoTrabalho planoTrabalho;

	private Date dataIndicacao;
	
	private Date dataFinalizacao;
	
	private MembroProjetoDiscente bolsistaAnterior;

	/** Construtor Padrão */
	public MovimentoIndicarBolsista(){
		
	}
	
	public Date getDataFinalizacao() {
		return dataFinalizacao;
	}

	public void setDataFinalizacao(Date dataFinalizacao) {
		this.dataFinalizacao = dataFinalizacao;
	}

	public Date getDataIndicacao() {
		return dataIndicacao;
	}

	public void setDataIndicacao(Date dataIndicacao) {
		this.dataIndicacao = dataIndicacao;
	}

	public PlanoTrabalho getPlanoTrabalho() {
		return planoTrabalho;
	}

	public void setPlanoTrabalho(PlanoTrabalho planoTrabalho) {
		this.planoTrabalho = planoTrabalho;
	}

	public MembroProjetoDiscente getBolsistaAnterior() {
		return bolsistaAnterior;
	}

	public void setBolsistaAnterior(MembroProjetoDiscente bolsistaAnterior) {
		this.bolsistaAnterior = bolsistaAnterior;
	}
	
}
