/*
 * EmprestimoByDataEmprestimoComparator.java
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
import java.util.Date;

import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;

/**
 *
 * Classe que ordena os emprestimos pela data do emprestimo
 *
 * @author jadson
 * @since 20/11/2008
 * @version 1.0 criacao da classe
 *
 */
public class EmprestimoByDataEmprestimoComparator implements Comparator<Emprestimo>{

	public int compare(Emprestimo o1, Emprestimo o2) {
		
		Date data1 = o1.getDataEmprestimo();
		
		Date data2 = o2.getDataEmprestimo();
		
		// se a data 2 for maior o resultado de dias entre as data vai ser > 0 
		
		return CalendarUtils.calculoDiasIntervalosFechados(data2, data1);
		
	}

}
