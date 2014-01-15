/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/10/2008
 *
 */

package br.ufrn.sigaa.biblioteca.integracao.dtos;

import java.io.Serializable;

/**
 * 
 * Classe que cont�m as informa��es do tipo de empr�stimo, que s�o passadas ao
 * sistemas desktop de empr�stimos.
 * 
 * @author Fred_Castro
 * @since 20/10/2008
 * @version 1.0 cria��o da classe
 * 
 */

public class TipoEmprestimoDto implements Comparable<TipoEmprestimoDto>, Serializable {

	private static final long serialVersionUID = 1L;

	public int idTipoEmprestimo; // Uma refer�ncia ao TipoEmprestimo verdadeiro
	public String descricao; // A descri��o do TipoEmprestimo

	
	/**
     * Construtor default, obrigat�rios nos DTOs.
     */
	public TipoEmprestimoDto() {
		super();
	}



	/**
	 * Este m�todo � a implementa��o do m�todo compareTo, da interface Comparable.
	 * Auxilia na ordena��o de objetos TipoEmprestimoDto em listas.
	 * O crit�rio para ordenar � a id, em ordem crescente.
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(TipoEmprestimoDto tipo) {
		return new Integer(idTipoEmprestimo).compareTo(tipo.idTipoEmprestimo);
	}
}