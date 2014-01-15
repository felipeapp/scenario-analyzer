/*
 * MaterialByCodigoDeBarrasComparator.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.Comparator;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;

/**
 *
 *    Compara dois materiais pelo c�digo de barras. 
 *
 * @author jadson
 * @since 10/08/2009
 * @version 1.0 criacao da classe
 *
 */
public class MaterialByCodigoDeBarrasComparator implements Comparator<MaterialInformacional>{

	
	public int compare(MaterialInformacional o1, MaterialInformacional o2) {
		
		return o1.getCodigoBarras().compareTo(o2.getCodigoBarras());
		
	}
}
