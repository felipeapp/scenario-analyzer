/*
 * CacheEntidadesMarcByNumeroSistemaComparator.java
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

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;

/**
 *
 *   Ordena as entidades MARC pelo número do sistema.
 *
 * @author jadson
 * @since 07/07/2009
 * @version 1.0 criacao da classe
 *
 */
public class CacheEntidadesMarcByNumeroSistemaComparator implements Comparator<CacheEntidadesMarc>{

	public int compare(CacheEntidadesMarc arg0, CacheEntidadesMarc arg1) {
		return new Integer( arg0.getNumeroDoSistema()).compareTo(new Integer(arg1.getNumeroDoSistema()));
	}

}
