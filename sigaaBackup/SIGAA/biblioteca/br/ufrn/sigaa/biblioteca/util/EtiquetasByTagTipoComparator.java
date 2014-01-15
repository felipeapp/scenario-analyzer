/*
 * EtiquetasByTagTipoComparator.java
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

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;

/**
 *
 * Compara etiquetas pela tag e o tipo (Bibliografica ou Autoriades)
 *
 * @author jadson
 * @since 25/06/2009
 * @version 1.0 criacao da classe
 *
 */
public class EtiquetasByTagTipoComparator implements Comparator<Etiqueta>{

	public int compare(Etiqueta arg0, Etiqueta arg1) {

		int resultado = arg0.getTag().compareTo(arg1.getTag());
		
		if(resultado == 0){
			return new Integer(arg0.getTipo()).compareTo(new Integer(arg1.getTipo()));
		}else
			return resultado;
	}

}
