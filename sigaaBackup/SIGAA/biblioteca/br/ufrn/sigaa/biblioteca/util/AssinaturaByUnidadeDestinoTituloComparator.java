/* 
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/08/2009
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.Comparator;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;

/**
 *   Compara as assinatura pela unidade de destino e título das mesmas.
 *
 * @author jadson
 * @since 18/08/2009
 * @version 1.0 criacao da classe
 *
 */
public class AssinaturaByUnidadeDestinoTituloComparator implements Comparator<Assinatura>{

	
	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Assinatura arg0, Assinatura arg1) {
		
		if(arg0.getUnidadeDestino() != null || arg0.getUnidadeDestino() != null )
			return new Integer(arg0.getUnidadeDestino().getId()).compareTo( new Integer(arg1.getUnidadeDestino().getId())  );
		else{
			
			if(arg0.getUnidadeDestino() == null)
				return 1;
			else
				if(arg1.getUnidadeDestino() == null)
					return -1;
				else
					return 0;
		}
		
	}

}
