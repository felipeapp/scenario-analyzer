/*
 * ValorIndicadorByValorComparator.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.Comparator;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ValorIndicador;

/**
 *
 *    Compara os valores dos indicadores dos descritores dos campos do formato MARC pelo valor. 
 *
 * @author jadson
 * @since 28/08/2009
 * @version 1.0 criacao da classe
 *
 */
public class ValorIndicadorByValorComparator implements Comparator<ValorIndicador>{

	public int compare(ValorIndicador o1, ValorIndicador o2) {
		
		if(o1 != null && o2 != null)
			return o1.getValor().compareTo(o2.getValor());
		else{
			
			if(o1 != null && o2  == null)
				return 1;
			else{
				if(o1 == null && o2  != null)
					return -1;
				else
					return 0;
			}
		}
	}

}
