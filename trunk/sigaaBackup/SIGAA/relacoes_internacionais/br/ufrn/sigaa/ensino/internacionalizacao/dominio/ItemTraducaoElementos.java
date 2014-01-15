/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 16/07/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.dominio;

import java.util.Collection;

/**
 * Classe auxiliar para cadastro de tradução de elementos por atributo.
 * 
 * @author Rafael Gomes
 *
 */
public class ItemTraducaoElementos {

	/** Objeto responsável pela manutenção dos campos/atributos que serão traduzidos no sistema. */
	ItemTraducao itemTraducao;
	/** Coleção da tradução dos elementos do histórico do discente.*/
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
