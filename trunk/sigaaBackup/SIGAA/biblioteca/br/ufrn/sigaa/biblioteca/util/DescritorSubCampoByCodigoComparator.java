/*
 * DescritorSubCampoByCodigoComparator.java
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

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.DescritorSubCampo;

/**
 *
 *   Compra dois descritores de subcampo pelo código.
 *
 * @author jadson
 * @since 12/05/2009
 * @version 1.0 criacao da classe
 *
 */
public class DescritorSubCampoByCodigoComparator implements Comparator<DescritorSubCampo>{

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(DescritorSubCampo arg0, DescritorSubCampo arg1) {
		
		if(arg0 != null && arg1 != null){
			
			if(arg0.getCodigo() != null && arg1.getCodigo() != null){
				
				// Subcampos com códigos inteiros devem vir depois dos subcampos com letras
				if( Character.isDigit(arg0.getCodigo()) && !Character.isDigit(arg1.getCodigo())){
					return 1;
				}else{
					if( ! Character.isDigit(arg0.getCodigo()) && Character.isDigit(arg1.getCodigo())){
						return -1;
					}else{
						if( Character.isDigit(arg0.getCodigo()) && Character.isDigit(arg1.getCodigo())){
							return new Integer(arg0.getCodigo()).compareTo(new Integer(arg1.getCodigo()));
						}else{
							// ! Character.isDigit(arg0.getCodigo()) && ! Character.isDigit(arg1.getCodigo())
							return arg0.getCodigo().compareTo(arg1.getCodigo());
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
