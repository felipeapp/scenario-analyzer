/*
 * MovimentoRenovaAssinaturaPeriodico.java
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
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.RenovacaoAssinatura;

/**
 *   Passsa os dados para o processador que realiza a renovação.
 *
 * @author jadson
 * @since 16/11/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoRenovaAssinaturaPeriodico extends AbstractMovimentoAdapter{

	private RenovacaoAssinatura renovacao; // contém a nova data da assinatuar que foi digitado pelo usuário da página
	
	private Assinatura assinatura; // a assinatura que vai ser renovada

	public MovimentoRenovaAssinaturaPeriodico(RenovacaoAssinatura renovacao, Assinatura assinatura) {
		super();
		this.renovacao = renovacao;
		this.assinatura = assinatura;
	}

	public RenovacaoAssinatura getRenovacao() {
		return renovacao;
	}

	public Assinatura getAssinatura() {
		return assinatura;
	}

	
	
}
