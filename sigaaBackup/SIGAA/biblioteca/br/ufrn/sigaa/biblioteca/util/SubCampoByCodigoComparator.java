/*
 * SubCampoByCodigoComparator.java
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

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;

/**
 *
 * <p>Classe que ordena os sub campos pelos códigos.</p>
 * 
 * <p>Utilizadas apenas quanto se deseja visualizar todos os sub campos de um campo ordenados. Visto que na catalogação a ordem de 
 * inclusão o campo deve ser mantida.</p>
 *
 * @author jadson
 * @since 09/09/2008
 * @version 1.0 criacao da classe
 */
public class SubCampoByCodigoComparator implements Comparator<SubCampo>{

	/**
	 * Ordema pelo codigo a -> b -> d -> x ... 
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(SubCampo o1, SubCampo o2) {
		
		if(o1 != null && o2 != null){
		
			if(o1.getCodigo() != null && o2.getCodigo() != null){
				
				// Sub campos com códigos inteiros devem vim depois dos subcampos com letras
				if( Character.isDigit(o1.getCodigo()) && !Character.isDigit(o2.getCodigo())){
					return 1;
				}else{
					if( ! Character.isDigit(o1.getCodigo()) && Character.isDigit(o2.getCodigo())){
						return -1;
					}else{
						if( Character.isDigit(o1.getCodigo()) && Character.isDigit(o2.getCodigo())){
							return new Integer(o1.getCodigo()).compareTo(new Integer(o2.getCodigo()));
						}else{
							// ! Character.isDigit(o1.getCodigo()) && ! Character.isDigit(o2.getCodigo())
							return o1.getCodigo().compareTo(o2.getCodigo());
						}
					}
				}
				
			}else 
				return 0;
		}else{
			return 0;
		}
	}

}
