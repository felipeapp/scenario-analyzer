/*
 * ExemplarByLocalizacaoBibliotecaComparator.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.Comparator;

import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;

/**
 *
 *    Classe que compara dois exemplares pela Biblioteca e Localização (número de chamada)
 *
 * @author jadson
 * @since 21/05/2009
 * @version 1.0 criacao da classe
 *
 */
public class ExemplarByLocalizacaoBibliotecaComparator implements Comparator<Exemplar>{

	/**
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Exemplar e1, Exemplar e2) {

		int resultado = 0;
		
		if(e1.getBiblioteca()!= null && e2.getBiblioteca() != null){
			resultado = e1.getBiblioteca().getDescricao().compareTo(e2.getBiblioteca().getDescricao());
		
			if(resultado == 0){
				
				if(StringUtils.notEmpty(e1.getNumeroChamada()) && StringUtils.notEmpty(e2.getNumeroChamada()))
					return  e1.getNumeroChamada().compareTo(e2.getNumeroChamada());
			}else
				return resultado;
		
		}
		
		return resultado;
	}

}
