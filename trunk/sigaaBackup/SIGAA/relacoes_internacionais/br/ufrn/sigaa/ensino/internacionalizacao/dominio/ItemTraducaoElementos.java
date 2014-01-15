/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 16/07/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.dominio;

import java.util.Collection;

/**
 * Classe auxiliar para cadastro de tradu��o de elementos por atributo.
 * 
 * @author Rafael Gomes
 *
 */
public class ItemTraducaoElementos {

	/** Objeto respons�vel pela manuten��o dos campos/atributos que ser�o traduzidos no sistema. */
	ItemTraducao itemTraducao;
	/** Cole��o da tradu��o dos elementos do hist�rico do discente.*/
	Collection<TraducaoElemento> elementos;
	
	
	/**
	 * Minimal Constructor
	 */
	public ItemTraducaoElementos() {}

	/**
	 * Default COnstructor
	 * @param itemTraducao
	 * @param elementos
	 */
	public ItemTraducaoElementos(ItemTraducao itemTraducao,
			Collection<TraducaoElemento> elementos) {
		super();
		this.itemTraducao = itemTraducao;
		this.elementos = elementos;
	}

	public ItemTraducao getItemTraducao() {
		return itemTraducao;
	}

	public void setItemTraducao(ItemTraducao itemTraducao) {
		this.itemTraducao = itemTraducao;
	}

	public Collection<TraducaoElemento> getElementos() {
		return elementos;
	}

	public void setElementos(Collection<TraducaoElemento> elementos) {
		this.elementos = elementos;
	}
	
}
