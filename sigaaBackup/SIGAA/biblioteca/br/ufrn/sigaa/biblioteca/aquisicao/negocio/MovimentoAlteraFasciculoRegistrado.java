/*
 * MovimentoAlteraFasciculoRegistrado.java
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
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 *   Passa os dados para o processador ProcessadorAlteraFasciculoRegistrado
 *
 * @author jadson
 * @since 20/11/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoAlteraFasciculoRegistrado extends AbstractMovimentoAdapter{

	private Fasciculo fasciculo;
	
	private Assinatura assinaturaDoFasciculo;
	
	private boolean removendo = false; //indica quando vai apenas alterar os dados ou remover o fasciculos.

	public MovimentoAlteraFasciculoRegistrado(Fasciculo fasciculo,Assinatura assinaturaDoFasciculo, boolean removendo) {
		this.fasciculo = fasciculo;
		this.assinaturaDoFasciculo = assinaturaDoFasciculo;
		this.removendo = removendo;
	}

	public Fasciculo getFasciculo() {
		return fasciculo;
	}

	public Assinatura getAssinaturaDoFasciculo() {
		return assinaturaDoFasciculo;
	}

	public boolean isRemovendo() {
		return removendo;
	}

	
	
}
