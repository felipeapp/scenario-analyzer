/*
 * AssinaturaByTituloComparator.java
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

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;

/**
 *
 *   Compra assinaturas pelo Título dela.
 *
 * @author jadson
 * @since 14/08/2009
 * @version 1.0 criacao da classe
 *
 */
public class AssinaturaByTituloComparator implements Comparator<Assinatura>{

	public int compare(Assinatura arg0, Assinatura arg1) {
		
		if(arg0.getTitulo() == null || arg1.getTitulo() == null )
			return 0;
		else
			return arg0.getTitulo().compareTo(arg1.getTitulo());
	}
	
}
