/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/06/2008
 *
 */
package br.ufrn.sigaa.vestibular.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;

/** Classe que encapsula dados para o processamento de marcação de presença em reunião.
 * @author Édipo Elder F. Melo
 */
public class MovimentoPresencaReuniaoFiscal extends AbstractMovimentoAdapter{
	
	/** Lista de fiscais a processar. */
	List<Fiscal> listaFiscal;

	/** Retorna a lista de fiscais a processar.
	 * @return
	 */
	public List<Fiscal> getListaFiscal() {
		return listaFiscal;
	}

	/** Seta a lista de fiscais a processar.
	 * @param listaFiscal
	 */
	public void setListaFiscal(List<Fiscal> listaFiscal) {
		this.listaFiscal = listaFiscal;
	}

}
