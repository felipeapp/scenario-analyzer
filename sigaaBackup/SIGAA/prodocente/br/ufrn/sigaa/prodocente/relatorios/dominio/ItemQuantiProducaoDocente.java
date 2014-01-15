/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '17/01/2007'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 *  Representa o item do relatório quantitativo de produções intelectual de uma
 * unidade agrupado por docentes
 * @author André
 *
 */
public class ItemQuantiProducaoDocente {


	private String nomeDocente;

	private Collection<ItemQuantitativoProducao> itens = new ArrayList<ItemQuantitativoProducao>();

	public Collection<ItemQuantitativoProducao> getItens() {
		return itens;
	}

	public void setItens(Collection<ItemQuantitativoProducao> itens) {
		this.itens = itens;
	}

	public String getNomeDocente() {
		return nomeDocente;
	}

	public void setNomeDocente(String nomeDocente) {
		this.nomeDocente = nomeDocente;
	}

	@Override
	public String toString() {
		return nomeDocente + " {\n"+itens+"\n}\n";
	}
}
