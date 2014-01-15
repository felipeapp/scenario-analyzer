/*
 * Sistema Integrado de Patrim�nio e Administra��o de Contratos
 * Superintend�ncia de Inform�tica - UFRN
 * 
 * Created on 23/10/2006
 *
 */
package br.ufrn.sigaa.monitoria.dominio;

import java.util.Comparator;

/*******************************************************************************
 * Comparator para classificar projetos por ordem de m�dia final.
 * 
 * @author David Ricardo
 * 
 ******************************************************************************/
public class ProjetoComparator implements Comparator<ProjetoEnsino> {

	public int compare(ProjetoEnsino p1, ProjetoEnsino p2) {
		return p1.compareTo(p2);
	}

}
