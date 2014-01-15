/*
 * TestaRemocaoPontuacaoPesquisas.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.testes;

import junit.framework.TestCase;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;

/**
 *
 * TODO Insira seu comentario aqui
 *
 * @author jadson
 * @since 02/12/2009
 * @version 1.0 criacao da classe
 *
 */
public class TestaRemocaoPontuacaoPesquisas extends TestCase {

	/**
	 * Test method for {@link br.ufrn.sigaa.biblioteca.util.BibliotecaUtil#retiraPontuacaoACR2(java.lang.String)}.
	 */
	public void testRetiraPontuacaoACR2SemPontuacao() {
		String retorno = CatalogacaoUtil.retiraPontuacaoCamposBuscas("jadson");
	
		System.out.println(retorno);
		
		assertEquals("jadson", retorno);
	}
	
	/**
	 * Test method for {@link br.ufrn.sigaa.biblioteca.util.BibliotecaUtil#retiraPontuacaoACR2(java.lang.String)}.
	 */
	public void testRetiraPontuacaoACR2ComTodasAsPontuacoes() {
		String retorno = CatalogacaoUtil.retiraPontuacaoCamposBuscas("a/:.-,!?@\"'#$%¨&*\\_=+§|[]{}()a");
		
		System.out.println(retorno);
		
		assertEquals("a                            a", retorno);
	}
	
	/**
	 * Test method for {@link br.ufrn.sigaa.biblioteca.util.BibliotecaUtil#retiraPontuacaoACR2(java.lang.String)}.
	 */
	public void testRetiraPontuacaoACR2ComTodasAsPontuacoesMisturadas() {
		String retorno = CatalogacaoUtil.retiraPontuacaoCamposBuscas("a/ a: a.a-a,a! a?a@a#a$a%a¨a&a*a\\a_a=a+a§a|a[a]a{a}a(a)a");
		
		System.out.println(retorno);
		
		assertEquals(retorno.length(), 56);
	}
	
	
	/**
	 * Test method for {@link br.ufrn.sigaa.biblioteca.util.BibliotecaUtil#retiraPontuacaoACR2(java.lang.String)}.
	 */
	public void testRetiraPontuacaoACR2CaracteresCoringa() {
		String retorno = CatalogacaoUtil.retiraPontuacaoCamposBuscas("a \\ \\ . \\\\  x*.*8 *** $$ $$ 123 ??? . a");
		
		System.out.println(retorno);
		
		assertEquals(retorno.length(), 39);
	}
	
	
	/**
	 * Test method for {@link br.ufrn.sigaa.biblioteca.util.BibliotecaUtil#retiraPontuacaoACR2(java.lang.String)}.
	 */
	public void testRetiraPontuacaoACR2CaracteresFazendoPartaDaPalavra() {
		String retorno = CatalogacaoUtil.retiraPontuacaoCamposBuscas("d'agua");
		
		System.out.println(retorno);
		
		assertEquals(retorno.length(), 6);
	}
	
	/**
	 * Test method for {@link br.ufrn.sigaa.biblioteca.util.BibliotecaUtil#retiraPontuacaoACR2(java.lang.String)}.
	 */
	public void testRetiraPontuacaoACR2CaracteresFazendoPartaDaPalavra2() {
		String retorno = CatalogacaoUtil.retiraPontuacaoCamposBuscas("Teste[1900]");
		
		System.out.println(retorno);
		
		assertEquals(retorno.length(), 11);
	}
	
	/**
	 * Test method for {@link br.ufrn.sigaa.biblioteca.util.BibliotecaUtil#retiraPontuacaoACR2(java.lang.String)}.
	 */
	public void testRetiraPontuacaoACR2CaracteresFazendoPartaDaPalavra3() {
		String retorno = CatalogacaoUtil.retiraPontuacaoCamposBuscas("\"Heranças políticas\"");
		
		System.out.println(retorno);
		
		assertEquals(retorno.length(), 20);
	}
}
