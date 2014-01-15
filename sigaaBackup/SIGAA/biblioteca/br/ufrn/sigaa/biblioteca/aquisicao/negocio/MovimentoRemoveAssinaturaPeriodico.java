/*
 * MovimentoRemoveAssinaturaPeriodico.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.aquisicao.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;

/**
 *
 * Passa os dados para o processador ProcessadorRemoveAssinaturaPeriodico.
 *
 * @author jadson
 * @since 19/11/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoRemoveAssinaturaPeriodico extends AbstractMovimentoAdapter{

	
	private Assinatura assinatura; // a assinatura que vai ser removida

	
	public MovimentoRemoveAssinaturaPeriodico(Assinatura assinatura) {
		this.assinatura = assinatura;
	}


	public Assinatura getAssinatura() {
		return assinatura;
	}	
	
}
