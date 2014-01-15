/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import org.springframework.context.ApplicationContext;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.integracao.interfaces.InclusaoBolsaAcademicaRemoteService;

/** 
 * Classe que encapsula os dados para o processamento da movimentação do discente.
 * 
 * @author Édipo Elder F. Melo
 *
 */
@SuppressWarnings("serial")
public class MovimentoMovimentacaoAluno extends MovimentoCadastro {

	ApplicationContext context;
	
	/** MBean remoto que fará a operação de solicitação exclusão de bolsas no SIPAC. */
	private InclusaoBolsaAcademicaRemoteService inclusaoBolsaBean;
	
	/** Retorna o MBean remoto que fará a operação de solicitação exclusão de bolsas no SIPAC. 
	 * @return
	 */
	public InclusaoBolsaAcademicaRemoteService getInclusaoBolsaBean() {
		return inclusaoBolsaBean;
	}

	/** Seta o MBean remoto que fará a operação de solicitação exclusão de bolsas no SIPAC. 
	 * @param inclusaoBolsaBean
	 */
	public void setInclusaoBolsaBean(
			InclusaoBolsaAcademicaRemoteService inclusaoBolsaBean) {
		this.inclusaoBolsaBean = inclusaoBolsaBean;
	}

	public ApplicationContext getContext() {
		return context;
	}

	public void setContext(ApplicationContext context) {
		this.context = context;
	}

}
