/*
 * DescritorCampoControleByPosicaoInicialComparator.java
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

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.DescritorCampoControle;

/**
 *
 * Compara dois descritores de campos de controle pela posicao inicial
 *
 * @author jadson
 * @since 12/05/2009
 * @version 1.0 criacao da classe
 *
 */
public class DescritorCampoControleByPosicaoInicialComparator  implements Comparator< DescritorCampoControle>{

	public int compare(DescritorCampoControle o1, DescritorCampoControle o2) {
		return new Integer( o1.getPosicaoInicio()).compareTo(new Integer(o2.getPosicaoInicio()));
	}

}
