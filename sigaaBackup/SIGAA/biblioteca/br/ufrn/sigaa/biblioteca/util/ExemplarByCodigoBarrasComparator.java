/*
 * ExemplarByCodigoBarrasComparator.java
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

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;

/**
 *
 * Classe que implemeta a logica de ordenação de um Item pelo numero do patrimonio e sub-biblioteca
 *
 * @author jadson
 * @since 09/09/2008
 * @version 1.0 criacao da classe
 *
 */
public class ExemplarByCodigoBarrasComparator  implements Comparator<Exemplar>{

	public int compare(Exemplar o1, Exemplar o2) {
		
		return o1.getCodigoBarras().compareTo(o2.getCodigoBarras());
		
	}

}
