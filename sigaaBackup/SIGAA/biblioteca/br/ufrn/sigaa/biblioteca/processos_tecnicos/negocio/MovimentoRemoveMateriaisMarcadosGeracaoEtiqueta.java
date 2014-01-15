/*
 * MovimentoRemoveMateriaisMarcadosGeracaoEtiqueta.java
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

/**
 *
 *      Passa os dados para o processador {@link ProcessadorRemoveMateriaisMarcadosGeracaoEtiqueta}
 *
 * @author jadson
 * @since 19/10/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoRemoveMateriaisMarcadosGeracaoEtiqueta extends AbstractMovimentoAdapter{

	/** Lista com os ids dos Exemplares a serem passados para o processador */
	private List<Integer> idsMateriais;

	public List<Integer> getIdsMateriais() {
		return idsMateriais;
	}
	
	public MovimentoRemoveMateriaisMarcadosGeracaoEtiqueta(List<Integer> idsMateriais) {
		this.idsMateriais = idsMateriais;
	}
	
}
