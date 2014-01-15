/*
 * DataUtilTest.java
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
package br.ufrn.sigaa.biblioteca.testes;

import java.util.Calendar;

import junit.framework.TestCase;
import br.ufrn.sigaa.biblioteca.util.CirculacaoUtil;

/**
 *
 * Classe que testa os calculos realizados na classe br.ufrn.sigaa.biblioteca.util.DataUtil
 *
 *
 * @author jadson
 * @since 02/01/2009
 * @version 1.0 criacao da classe
 *
 */
public class CalculaDiasEmAtrasoTest extends TestCase {

	/**
	 * Test method for {@link CirculacaoUtil#calculaDiasEmAtrasoBiblioteca(java.util.Date, java.util.Date)}.
	 */
	public void testCalculaQuantidadeDiasEntreDatasMesmoAno() {
		// 10-01-2009
		Calendar c3 = Calendar.getInstance();
		c3.set(Calendar.DAY_OF_MONTH, 10);
		c3.set(Calendar.MONTH, 0);
		c3.set(Calendar.YEAR, 2009);
		
		// 31-01-2009
		Calendar c4 = Calendar.getInstance();
		c4.set(Calendar.DAY_OF_MONTH, 31);
		c4.set(Calendar.MONTH, 0);
		c4.set(Calendar.YEAR, 2009);
		
		
		assertEquals(21, CirculacaoUtil.calculaDiasEmAtrasoBiblioteca(c3.getTime(), c4.getTime()));
	}

	/**
	 * Test method for  {@link CirculacaoUtil#calculaDiasEmAtrasoBiblioteca(java.util.Date, java.util.Date)}.
	 */
	public void testCalculaQuantidadeDiasEntreAnosDiferentes() {
		
		// 10-01-2009
		Calendar c1 = Calendar.getInstance();
		c1.set(Calendar.DAY_OF_MONTH, 10);
		c1.set(Calendar.MONTH, 0);
		c1.set(Calendar.YEAR, 2009);
		
		// 10-02-2012
		Calendar c2 = Calendar.getInstance();
		c2.set(Calendar.DAY_OF_MONTH, 10);
		c2.set(Calendar.MONTH, 1);
		c2.set(Calendar.YEAR, 2012);
		
		// (365 - 10) dias = de 2009
		// 365 dias = de 2010
		// 365 dias = de 2011
		// 31 + 10 dias = de 2012
		//-------
		// 1126
		
		assertEquals(1126, CirculacaoUtil.calculaDiasEmAtrasoBiblioteca(c1.getTime(), c2.getTime()));
	}
	
	
	/**
	 * Test method for  {@link CirculacaoUtil#calculaDiasEmAtrasoBiblioteca(java.util.Date, java.util.Date)}.
	 */
	public void testCalculaQuantidadeDiasMesmoDia() {
		
		// 10-01-2009
		Calendar c1 = Calendar.getInstance();
		c1.set(Calendar.DAY_OF_MONTH, 10);
		c1.set(Calendar.MONTH, 0);
		c1.set(Calendar.YEAR, 2009);
		
		// 10-01-2009
		Calendar c2 = Calendar.getInstance();
		c2.set(Calendar.DAY_OF_MONTH, 10);
		c2.set(Calendar.MONTH, 0);
		c2.set(Calendar.YEAR, 2009);
		
		assertEquals(0, CirculacaoUtil.calculaDiasEmAtrasoBiblioteca(c1.getTime(), c2.getTime()));
	}
	
	
	/**
	 * Test method for {@link CirculacaoUtil#calculaDiasEmAtrasoBiblioteca(java.util.Date, java.util.Date)}.
	 */
	public void testCalculaQuantidadeDiasMesmoDiaComHoras() {
		
		// 10-01-2009 as 12h 
		Calendar c1 = Calendar.getInstance();
		c1.set(Calendar.DAY_OF_MONTH, 10);
		c1.set(Calendar.MONTH, 0);
		c1.set(Calendar.YEAR, 2009);
		c1.set(Calendar.HOUR_OF_DAY, 12);
		
		// 10-01-2009 as 14h
		Calendar c2 = Calendar.getInstance();
		c2.set(Calendar.DAY_OF_MONTH, 10);
		c2.set(Calendar.MONTH, 0);
		c2.set(Calendar.YEAR, 2009);
		c2.set(Calendar.HOUR_OF_DAY, 14);
		
		assertEquals(0, CirculacaoUtil.calculaDiasEmAtrasoBiblioteca(c1.getTime(), c2.getTime()));
	}
	
	/**
	 * Test method for {@link CirculacaoUtil#calculaDiasEmAtrasoBiblioteca(java.util.Date, java.util.Date)}.
	 */
	public void testCalculaQuantidadeDiasDiaSequinteComHora() {
		
		// 10-01-2009 12h
		Calendar c1 = Calendar.getInstance();
		c1.set(Calendar.DAY_OF_MONTH, 10);
		c1.set(Calendar.MONTH, 0);
		c1.set(Calendar.YEAR, 2009);
		c1.set(Calendar.HOUR_OF_DAY, 12);
		
		// 11-01-2009 14h
		Calendar c2 = Calendar.getInstance();
		c2.set(Calendar.DAY_OF_MONTH, 11);
		c2.set(Calendar.MONTH, 0);
		c2.set(Calendar.YEAR, 2009);
		c2.set(Calendar.HOUR_OF_DAY, 14);
		
		assertEquals(1, CirculacaoUtil.calculaDiasEmAtrasoBiblioteca(c1.getTime(), c2.getTime()));
	}
	
	/**
	 * Test method for {@link CirculacaoUtil#calculaDiasEmAtrasoBiblioteca(java.util.Date, java.util.Date)}.
	 */
	public void testCalculaQuantidadeDiasDiaSequinteMasComHoraMenosQue24() {
		
		// 10-01-2009 12h
		Calendar c1 = Calendar.getInstance();
		c1.set(Calendar.DAY_OF_MONTH, 10);
		c1.set(Calendar.MONTH, 0);
		c1.set(Calendar.YEAR, 2009);
		c1.set(Calendar.HOUR_OF_DAY, 12);
		
		// 11-01-2009 10h
		Calendar c2 = Calendar.getInstance();
		c2.set(Calendar.DAY_OF_MONTH, 11);
		c2.set(Calendar.MONTH, 0);
		c2.set(Calendar.YEAR, 2009);
		c2.set(Calendar.HOUR_OF_DAY, 10);
		
		assertEquals(1, CirculacaoUtil.calculaDiasEmAtrasoBiblioteca(c1.getTime(), c2.getTime()));
	}
	
}
