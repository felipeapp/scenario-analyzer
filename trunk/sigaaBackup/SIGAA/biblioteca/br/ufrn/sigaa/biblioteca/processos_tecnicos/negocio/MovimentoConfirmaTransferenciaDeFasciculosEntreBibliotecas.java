/*
 * MovimentoConfirmaTransferenciaDeFasciculosEntreBibliotecas.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 *
 * Passa os dados para o processador
 *
 * @author jadson
 * @since 26/11/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoConfirmaTransferenciaDeFasciculosEntreBibliotecas extends AbstractMovimentoAdapter{

	/** Os fascículos que o usuário confirmou a transferência ou não */ 
	private List<Fasciculo> fasciculos;

	/** Se o código de barras serão alterados para ficarem iguais aos códigos dos fascículos originais da assinatura destino.*/
	boolean codigoDeBarrasAcompanhaCodigoNovaAssinatura;
	
	/**
	 * O construtor.
	 * 
	 * @param fasciculos
	 */
	public MovimentoConfirmaTransferenciaDeFasciculosEntreBibliotecas(List<Fasciculo> fasciculos, boolean codigoDeBarrasAcompanhaCodigoNovaAssinatura) {
		this.fasciculos = fasciculos;
		this.codigoDeBarrasAcompanhaCodigoNovaAssinatura = codigoDeBarrasAcompanhaCodigoNovaAssinatura;
	}

	
	public List<Fasciculo> getFasciculos() {
		return fasciculos;
	}

	public boolean isCodigoDeBarrasAcompanhaCodigoNovaAssinatura() {
		return codigoDeBarrasAcompanhaCodigoNovaAssinatura;
	}
	
}
