/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/19 - 19:30:51
 */
package br.ufrn.sigaa.ead.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.ead.dominio.ItemPrograma;

/**
 * Classe de Movimentação de Item do Programa
 * @author David Pereira
 *
 */
public class MovimentoItemPrograma extends AbstractMovimentoAdapter {
	
	private ItemPrograma itemPrograma;
	
	public ItemPrograma getItemPrograma() {
		return itemPrograma;
	}

	public void setItemPrograma(ItemPrograma itemPrograma) {
		this.itemPrograma = itemPrograma;
	}

	public ItemPrograma getObjMovimentado() {
		return itemPrograma;
	}
	
}
