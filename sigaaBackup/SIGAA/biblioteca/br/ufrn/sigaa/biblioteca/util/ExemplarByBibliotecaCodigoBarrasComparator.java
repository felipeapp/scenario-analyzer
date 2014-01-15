/*
 * ExemplarByBibliotecaCodigoBarrasComparator.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 15/12/2009
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.Comparator;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Exemplar;

/**
 *
 * Compara dois exemplares pela biblioteca e c�digo de barras <br/><br/>
 * Para utilizar essa classe, a biblioteca do exemplar tem que est� populada.
 *
 * @author jadson
 * @since 15/12/2009
 * @version 1.0 criacao da classe
 *
 */
public class ExemplarByBibliotecaCodigoBarrasComparator implements Comparator<Exemplar>{

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Exemplar arg0, Exemplar arg1) {
		
		Integer idBiblioteca0 = arg0.getBiblioteca().getId();
		Integer idBiblioteca1 = arg1.getBiblioteca().getId();
		
		int resultado = idBiblioteca0.compareTo(idBiblioteca1);
		
		if(resultado == 0)
			resultado = arg0.getCodigoBarras().compareTo(arg1.getCodigoBarras());	
		
		return resultado;
	}
	

}
