/*
 * AssinaturaByUnidadeDestinoCodigoComparator.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 11/02/2010
 * Autor: jadson
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.Comparator;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;

/**
 *  Compara duas assinturas pela unidade de destino dos seus fascículos e pelo código da assinatura.
 *
 * @author jadson
 *
 */
public class AssinaturaByUnidadeDestinoCodigoComparator implements Comparator<Assinatura>{

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	
	public int compare(Assinatura arg0, Assinatura arg1) {
		if(arg0.getUnidadeDestino() != null || arg0.getUnidadeDestino() != null ){
			int restultado =  new Integer(arg0.getUnidadeDestino().getId()).compareTo( new Integer(arg1.getUnidadeDestino().getId())  );
			if(restultado != 0)
				return restultado;
			else
				return comparaByCodigo(arg0, arg1);
		}else{
			
			return comparaByCodigo(arg0, arg1);
		}
	}

		
		
	private int comparaByCodigo(Assinatura arg0, Assinatura arg1){
		return arg0.getCodigo().compareTo( arg1.getCodigo() );
	}
}
