/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 * 
 * Created on 23/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.dominio;

import java.util.Comparator;

/*******************************************************************************
 * Comparator para classificar projetos por ordem de média final.
 * 
 * @author David Ricardo
 * 
 ******************************************************************************/
public class ProjetoComparator implements Comparator<ProjetoEnsino> {

	public int compare(ProjetoEnsino p1, ProjetoEnsino p2) {
		return p1.compareTo(p2);
	}

}
