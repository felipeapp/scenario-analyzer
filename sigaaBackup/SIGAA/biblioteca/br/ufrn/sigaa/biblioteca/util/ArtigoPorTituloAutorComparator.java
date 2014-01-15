/*
 * ArtigoPorTituloAutorComparator.java
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
 *   Compara 2 artigos pelo título e autor.
 *
 * @author jadson
 * @since 23/07/2009
 * @version 1.0 criacao da classe
 *
 */
public class ArtigoPorTituloAutorComparator  implements Comparator<CacheEntidadesMarc>{

	public int compare(CacheEntidadesMarc arg0, CacheEntidadesMarc arg1) {
		int resultado = 0;
		
		if( arg0.getTitulo() != null && arg1.getTitulo() != null)
			resultado = arg0.getTitulo().compareTo(arg1.getTitulo());
		
		if(resultado == 0 && ( arg0.getAutor() != null && arg1.getAutor() != null) )
			return arg0.getAutor().compareTo(arg1.getAutor());
		else
			return resultado;
		
	}

}
