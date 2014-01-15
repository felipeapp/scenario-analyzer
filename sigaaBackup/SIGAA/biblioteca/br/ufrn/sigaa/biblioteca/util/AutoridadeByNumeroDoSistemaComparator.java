/*
 * AutoridadeByNumeroDoSistemaComparator.java
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

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;

/**
 *
 *    Ordena as autoridades pelo número do sistema.
 *
 * @author jadson
 * @since 01/06/2009
 * @version 1.0 criacao da classe
 *
 */
public class AutoridadeByNumeroDoSistemaComparator  implements Comparator<Autoridade>{

	public int compare(Autoridade o1, Autoridade o2) {
		return new Integer( o1.getNumeroDoSistema()).compareTo(new Integer(o2.getNumeroDoSistema()));
	}

}
