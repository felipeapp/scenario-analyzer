/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática
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
 * Movimento para inclusão de bolsas no sipac a partir de solicitação no sigaa.
 * 
 * @author Ilueny Santos
 *
 */
public class MovimentoBolsaAcademica extends AbstractMovimentoAdapter {

	/** MBean remoto que fará a inclusão no SIPAC. */
	private InclusaoBolsaAcademicaRemoteService inclusaoBolsaBean;
	
	/** Coleção de solicitações a serem realizadas no SIPAC. */
	private Collection<InclusaoBolsaAcademicaDTO> solicitacoes;

	/** Retorna o MBean remoto que fará a inclusão no SIPAC. 
	 * @return
	 */
	public InclusaoBolsaAcademicaRemoteService getInclusaoBolsaBean() {
		return inclusaoBolsaBean;
	}

	/** Seta o MBean remoto que fará a inclusão no SIPAC. 
	 * @param inclusaoBolsaBean
	 */
	public void setInclusaoBolsaBean(
			InclusaoBolsaAcademicaRemoteService inclusaoBolsaBean) {
		this.inclusaoBolsaBean = inclusaoBolsaBean;
	}

	/** Retorna uma coleção de solicitações a serem realizadas no SIPAC. 
	 * @return
	 */
	public Collection<InclusaoBolsaAcademicaDTO> getSolicitacoes() {
		return solicitacoes;
	}

	/** Seta uma coleção de solicitações a serem realizadas no SIPAC.
	 * @param solicitacoes
	 */
	public void setSolicitacoes(Collection<InclusaoBolsaAcademicaDTO> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}
	
}
