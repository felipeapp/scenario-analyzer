/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 *  Representa o item do relat�rio quantitativo de produ��es intelectual de uma
 * unidade agrupado por docentes
 * @author Andr�
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
