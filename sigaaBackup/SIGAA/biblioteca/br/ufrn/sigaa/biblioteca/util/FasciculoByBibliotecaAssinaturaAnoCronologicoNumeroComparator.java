/*
 * FasciculoByBibliotecaAssinaturaAnoCronologicoNumeroComparator.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 03/02/2010
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.Comparator;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 *
 *  Compara os fascículos pela biblioteca, assinatura, anoCronologico e número
 *
 * @author jadson
 * @since 03/02/2010
 * @version 1.0 criacao da classe
 *
 */
public class FasciculoByBibliotecaAssinaturaAnoCronologicoNumeroComparator implements Comparator<Fasciculo>{


		/**
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
			
			return comparaAssinatura(f1, f2);
		}
		
		/**
		 * Compara pela assinatura.
		 */
		private int comparaAssinatura(Fasciculo f1, Fasciculo f2){
			if( f1.getAssinatura() != null && f2.getAssinatura() != null ){
				if( !  new Integer( f1.getAssinatura().getId()).equals(  new Integer(f2.getAssinatura().getId()) ) ){
					return new Integer( f1.getAssinatura().getId()).compareTo(  new Integer(f2.getAssinatura().getId()) );
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
		 * ai da para comparar. Outro não intervalos: 1-20, ai fica por último
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