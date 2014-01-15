/*
 * CampoControleByEtiquetaComparator.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.Comparator;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CampoControle;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;

/**
 *
 * Classe que implementa as regras para ordenar campos de controle pela etiqueta
 *
 * @author jadson
 * @since 08/09/2008
 * @version 1.0 criacao da classe
 *
 */
public class CampoControleByEtiquetaComparator implements Comparator<CampoControle>{

	public int compare(CampoControle arg0, CampoControle arg1) {
		
		if(arg0 == null || arg1 == null) // alguns vezes o sistema pula alguns números de nas posições dos campos, e esses são recuperados como null pelo hibernate
			return 0;
		
		if(arg0.getEtiqueta() != null && arg1.getEtiqueta() != null ){
		
			String tag0 = arg0.getEtiqueta().getTag();
			String tag1 = arg1.getEtiqueta().getTag();
			
			// A leader vem sempre em primeiro
			if(tag0.equalsIgnoreCase(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO.getTag())){
				return -1;
			}else{
				if((tag1.equalsIgnoreCase(Etiqueta.CAMPO_LIDER_BIBLIOGRAFICO.getTag()))){
					return 1;
				}
			}   
			
			// tirando a lider o resto é números, pelo menos no padrão MARC é assim, não sei no aleph
			try{
				Integer tag0Numerica = Integer.parseInt(tag0);
				Integer tag1Numerica = Integer.parseInt(tag1);
				
				int resultado = tag0Numerica.compareTo(tag1Numerica);
				
				if(resultado == 0) // tags iguais, ordena pela posição
					return new Integer(arg0.getPosicao()).compareTo(new Integer(arg1.getPosicao()));
				else
					return resultado; // tag diferentes, ordena pela tag
				
			}catch (NumberFormatException nfe) {
				return 0;
			}
		
		}else{
			return 0;
		}
		
	}
	
}