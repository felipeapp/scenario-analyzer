/*
 * Universidade Federal do Rio Grande do Norte

 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 10/02/2010
 */
package br.ufrn.sigaa.bolsas.negocio;

import java.util.Collection;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.integracao.dto.InclusaoBolsaAcademicaDTO;
import br.ufrn.integracao.interfaces.InclusaoBolsaAcademicaRemoteService;

/**
 * Movimento para inclus�o de bolsas no sipac a partir de solicita��o no sigaa.
 * 
 * @author Ilueny Santos
 *
 */
public class MovimentoBolsaAcademica extends AbstractMovimentoAdapter {

	/** MBean remoto que far� a inclus�o no SIPAC. */
	private InclusaoBolsaAcademicaRemoteService inclusaoBolsaBean;
	
	/** Cole��o de solicita��es a serem realizadas no SIPAC. */
	private Collection<InclusaoBolsaAcademicaDTO> solicitacoes;

	/** Retorna o MBean remoto que far� a inclus�o no SIPAC. 
	 * @return
	 */
	public InclusaoBolsaAcademicaRemoteService getInclusaoBolsaBean() {
		return inclusaoBolsaBean;
	}

	/** Seta o MBean remoto que far� a inclus�o no SIPAC. 
	 * @param inclusaoBolsaBean
	 */
	public void setInclusaoBolsaBean(
			InclusaoBolsaAcademicaRemoteService inclusaoBolsaBean) {
		this.inclusaoBolsaBean = inclusaoBolsaBean;
	}

	/** Retorna uma cole��o de solicita��es a serem realizadas no SIPAC. 
	 * @return
	 */
	public Collection<InclusaoBolsaAcademicaDTO> getSolicitacoes() {
		return solicitacoes;
	}

	/** Seta uma cole��o de solicita��es a serem realizadas no SIPAC.
	 * @param solicitacoes
	 */
	public void setSolicitacoes(Collection<InclusaoBolsaAcademicaDTO> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}
	
}
