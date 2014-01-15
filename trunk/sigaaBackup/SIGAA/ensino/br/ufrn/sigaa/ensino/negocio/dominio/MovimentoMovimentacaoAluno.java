/*
 * Sistema Integrado de Patrim�nio e Administra��o de Contratos
 * Superintend�ncia de Inform�tica - UFRN
 */
package br.ufrn.sigaa.ensino.negocio.dominio;

import org.springframework.context.ApplicationContext;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.integracao.interfaces.InclusaoBolsaAcademicaRemoteService;

/** 
 * Classe que encapsula os dados para o processamento da movimenta��o do discente.
 * 
 * @author �dipo Elder F. Melo
 *
 */
@SuppressWarnings("serial")
public class MovimentoMovimentacaoAluno extends MovimentoCadastro {

	ApplicationContext context;
	
	/** MBean remoto que far� a opera��o de solicita��o exclus�o de bolsas no SIPAC. */
	private InclusaoBolsaAcademicaRemoteService inclusaoBolsaBean;
	
	/** Retorna o MBean remoto que far� a opera��o de solicita��o exclus�o de bolsas no SIPAC. 
	 * @return
	 */
	public InclusaoBolsaAcademicaRemoteService getInclusaoBolsaBean() {
		return inclusaoBolsaBean;
	}

	/** Seta o MBean remoto que far� a opera��o de solicita��o exclus�o de bolsas no SIPAC. 
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
