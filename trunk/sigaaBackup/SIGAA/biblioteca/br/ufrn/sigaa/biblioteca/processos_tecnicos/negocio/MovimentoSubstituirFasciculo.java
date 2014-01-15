/*
 * MovimentoSubstituirFasciculo.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 *
 *    Movimento que passa os dados da substituíção de um fascículo para o processador {@link ProcessadorSubstituirFasciculo}.
 *
 * @author jadson
 * @since 26/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoSubstituirFasciculo extends AbstractMovimentoAdapter{
	
	private Fasciculo fasciculoSubstituidor;
	/** Objeto para salvar no histórico */
	private Fasciculo fasciculoQueVaiSerSubstituido; 
	
	public MovimentoSubstituirFasciculo(Fasciculo fasciculoSubstituidor, Fasciculo fasciculoQueVaiSerSubstituido){
		this.fasciculoSubstituidor = fasciculoSubstituidor;
		this.fasciculoQueVaiSerSubstituido = fasciculoQueVaiSerSubstituido;
	}

	public Fasciculo getFasciculoSubstituidor() {
		return fasciculoSubstituidor;
	}

	public Fasciculo getFasciculoQueVaiSerSubstituido() {
		return fasciculoQueVaiSerSubstituido;
	}

	
}
