/*
 * ValorDescritorCampoControleByValorComparator.java
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

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ValorDescritorCampoControle;

/**
 *
 * Compara dois valores de descritores de campos de controle pelo valor
 *
 * @author jadson
 * @since 12/05/2009
 * @version 1.0 criacao da classe
 *
 */
public class ValorDescritorCampoControleByValorComparator implements Comparator< ValorDescritorCampoControle>{

	public int compare(ValorDescritorCampoControle o1, ValorDescritorCampoControle o2) {
		return o1.getValor().compareTo(o2.getValor());
	}

}
