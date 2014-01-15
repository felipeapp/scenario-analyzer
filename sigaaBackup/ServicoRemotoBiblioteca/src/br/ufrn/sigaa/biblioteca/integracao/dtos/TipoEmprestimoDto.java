/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/10/2008
 *
 */

package br.ufrn.sigaa.biblioteca.integracao.dtos;

import java.io.Serializable;

/**
 * 
 * Classe que contém as informações do tipo de empréstimo, que são passadas ao
 * sistemas desktop de empréstimos.
 * 
 * @author Fred_Castro
 * @since 20/10/2008
 * @version 1.0 criação da classe
 * 
 */

public class TipoEmprestimoDto implements Comparable<TipoEmprestimoDto>, Serializable {

	private static final long serialVersionUID = 1L;

	public int idTipoEmprestimo; // Uma referência ao TipoEmprestimo verdadeiro
	public String descricao; // A descrição do TipoEmprestimo

	
	/**
     * Construtor default, obrigatórios nos DTOs.
     */
	public TipoEmprestimoDto() {
		super();
	}



	/**
	 * Este método é a implementação do método compareTo, da interface Comparable.
	 * Auxilia na ordenação de objetos TipoEmprestimoDto em listas.
	 * O critério para ordenar é a id, em ordem crescente.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(TipoEmprestimoDto tipo) {
		return new Integer(idTipoEmprestimo).compareTo(tipo.idTipoEmprestimo);
	}
}