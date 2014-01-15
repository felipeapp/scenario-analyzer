/*
 * MovimentoConfirmaTransferenciaDeFasciculosEntreBibliotecas.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
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

	/** Os fasc�culos que o usu�rio confirmou a transfer�ncia ou n�o */ 
	private List<Fasciculo> fasciculos;

	/** Se o c�digo de barras ser�o alterados para ficarem iguais aos c�digos dos fasc�culos originais da assinatura destino.*/
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
