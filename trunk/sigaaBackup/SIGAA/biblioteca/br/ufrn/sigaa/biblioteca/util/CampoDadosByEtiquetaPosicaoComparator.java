/*
 * CampoDadosByEtiquetaPosicaoComparator.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoDados;

/**
 * <p>Classe que implementa as regras para ordenar campos de dados pela etiqueta e posição.</p>
 * 
 * <p>Campos de etiqueta diferentes devem ser ordenados pela etiqueta. 020, 100, 500, 700....</p>
 * 
 * <p>Se tiverem a mesma etiqueta fica ordenado pela posição que o catalogador colocou, isso ocorre 
 * para campos que podem ser repetidos.</p>
 *
 * @author jadson
 * @since 08/09/2008
 * @version 1.0 criacao da classe
 *
 */
public class CampoDadosByEtiquetaPosicaoComparator implements Comparator<CampoDados>{

	
	
	/**
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(CampoDados arg0, CampoDados arg1) {
		
		if(arg0 == null || arg1 == null) // alguns vezes o sistema pula alguns números de nas posições dos campos, e esses são recuperados como null pelo hibernate
			return 0;
		
		if(arg0.getEtiqueta() != null && arg1.getEtiqueta() != null){
		
			String tag0 = arg0.getEtiqueta().getTag();
			String tag1 = arg1.getEtiqueta().getTag();
			
			
			if( StringUtils.isNotEmpty(tag0) &&  StringUtils.isNotEmpty(tag1)){
				
				for (int t1 = 0; t1 < tag0.length(); t1++) {
					if( ! Character.isDigit( tag0.charAt(t1) )){
						return 1;
					}
				}
				
				for (int t2 = 0; t2 < tag1.length(); t2++) {
					if( ! Character.isDigit( tag1.charAt(t2) )){
						return -1;
					}
				}
				
				int resultado = new Integer(tag0).compareTo(new Integer(tag1)); // as duas sao sempre numericas ex 245 e 940
				
				
				if(resultado == 0){ // tags iguais, ordena pela posição
				
					if(arg0.getPosicao() == -1 && arg1.getPosicao() == -1){
						return -1;   // o 1º sempre é o menor para ficar na ordem que foi digitado
					}
					
					if( arg0.getPosicao() == -1) // 1º campo da comparação não persistindo ainda
						return 1;  //fica por ultimo na ordenação
					
					if(arg1.getPosicao() == -1)  // 2º campo da comparação não persistindo ainda
						return -1; // 2º campo fica por último
						
					// os dois campos persistidos, e tag iguais, retorna ordenado pela posição.
					return new Integer(arg0.getPosicao()).compareTo(new Integer(arg1.getPosicao()));
					
				}else
					return resultado; // tag diferentes, ordena pela tag
				
			}else{
				if( StringUtils.isEmpty(tag0) &&  StringUtils.isNotEmpty(tag1)){
					return 1;
				}else{
					if( StringUtils.isNotEmpty(tag0) &&  StringUtils.isEmpty(tag1)){
						return -1;
					}else{
						return 0;
					}
				}
			}
			
		}else{
			return 0;
		}
	}
	
	
}
