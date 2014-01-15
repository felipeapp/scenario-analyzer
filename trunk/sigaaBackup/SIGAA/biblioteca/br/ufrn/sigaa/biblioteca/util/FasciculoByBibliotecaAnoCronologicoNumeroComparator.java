/*
 * FasciculoByBibliotecaAnoCronologicoNumeroComparator.java
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

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 *
 *    Compara os fascículo pela biblioteca, ano, número. Nessa ordem.
 *
 * @author jadson
 * @since 25/03/2009
 * @version 1.0 criacao da classe
 *
 */
public class FasciculoByBibliotecaAnoCronologicoNumeroComparator implements Comparator<Fasciculo> {

	
	
	/**
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Fasciculo f1, Fasciculo f2) {
		return comparaBiblioteca(f1, f2);	
	}
	
	
	/**
	 * Compara pela biblioteca.
	 */
	private int comparaBiblioteca(Fasciculo f1, Fasciculo f2){
		if( f1.getBiblioteca() != null && f2.getBiblioteca() != null ){
			if( !  new Integer( f1.getBiblioteca().getId()).equals(  new Integer(f2.getBiblioteca().getId()) ) ){
				return new Integer( f1.getBiblioteca().getId()).compareTo(  new Integer(f2.getBiblioteca().getId()) );
			}
		}
		
		return comparaAnoCronologico(f1, f2);
	}
	
	/**
	 * Compra pelo AnoCronologico (2008, 2009) em ordem descrescente para fascículos na mesma biblioteca.
	 */
	private int comparaAnoCronologico(Fasciculo f1, Fasciculo f2){
		
		if( f1.getAnoCronologico() != null && f2.getAnoCronologico() != null ){
		
			if(!  f1.getAnoCronologico().equals(f2.getAnoCronologico())){
				
				return - f1.getAnoCronologico().compareTo(f2.getAnoCronologico()); // retorna em ordem decrescente do ano
				
			}else{
				return comparaNumero(f1, f2);
			}
		}else{
			if(f1.getAnoCronologico() != null && f2.getAnoCronologico() == null ){
				return -1;
			}else{
				if(f1.getAnoCronologico() == null && f2.getAnoCronologico() != null ){
					return 1;
				}else{
					return comparaNumero(f1, f2);
				}
			}
		}	

	}


	/**
	 * Compara pelo número para fasciculos do mesmo ano. alguns números são numéricos 1, 2, 3
	 * e a comparação é feita normalmente. Outro não intervalos: 1-20, esses ficam por último
	 */
	private int comparaNumero(Fasciculo f1, Fasciculo f2){
		
		String primeiroNumero = f1.getNumero();
		String segundoNumero = f2.getNumero();
		
		StringBuilder primeiroNumeroInteiro = new StringBuilder();
		StringBuilder segundoNumeroInteiro = new StringBuilder();
		
		
		if(primeiroNumero != null){
			for (int i = 0; i < primeiroNumero.length(); i++) {
				
				if( Character.isDigit( primeiroNumero.charAt(i) )){
					primeiroNumeroInteiro.append(primeiroNumero.charAt(i)); 
				}else{
					break; // para no primeiro não número
				}
				
			}
		}
		
		if(segundoNumero != null){
			for (int i = 0; i < segundoNumero.length(); i++) {
				
				if( Character.isDigit( segundoNumero.charAt(i) )){
					segundoNumeroInteiro.append(segundoNumero.charAt(i)); 
				}else{
					break; // para no primeiro não número
				}
				
			}
		}
		
		Integer numeroFasciculo1 = 0;
		Integer numeroFasciculo2 = 0;
		
		if(primeiroNumeroInteiro.toString().length() > 0)
			numeroFasciculo1 = Integer.parseInt( primeiroNumeroInteiro.toString() );
		
		if(segundoNumeroInteiro.toString().length() > 0)
			numeroFasciculo2 = Integer.parseInt( segundoNumeroInteiro.toString() );
		
		return numeroFasciculo1.compareTo(numeroFasciculo2);
		
	}
	
}
