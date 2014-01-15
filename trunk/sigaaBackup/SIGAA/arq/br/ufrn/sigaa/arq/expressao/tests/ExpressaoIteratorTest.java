/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/13 - 17:08:26
 */
package br.ufrn.sigaa.arq.expressao.tests;

import junit.framework.TestCase;
import br.ufrn.sigaa.arq.expressao.ExpressaoIterator;

/**
 * Testes unitários para a classe ExpressaoIterator
 * 
 * @author David Pereira
 *
 */
public class ExpressaoIteratorTest extends TestCase {

	/**
	 * Se for passado um array vazio de tokens, hasNext() deve retornar sempre
	 * false e next() deve retornar null; 
	 */
	public void testTokensVazios() {
		String[] tokens = new String[] { };
		ExpressaoIterator it = new ExpressaoIterator(tokens);
		assertFalse(it.hasNext());
		assertNull(it.next());
		assertFalse(it.hasNext());
		assertNull(it.next());
	}
	
	/**
	 * Se for passado um array null, hasNext() deve retornar false e next() deve
	 * retornar null;
	 */
	public void testArrayNull() {
		String[] tokens = null;
		ExpressaoIterator it = new ExpressaoIterator(tokens);
		assertFalse(it.hasNext());
		assertNull(it.next());
		assertFalse(it.hasNext());
		assertNull(it.next());
	}
	
	/**
	 * Se só existir um token no array, hasNext() deve dar true a primeira vez e
	 * next() deve retornar o token. O próximo hasNext() deve retornar false. 
	 */
	public void testUmToken() {
		String[] tokens = new String[] { "X" };
		ExpressaoIterator it = new ExpressaoIterator(tokens);
		assertTrue(it.hasNext());
		assertEquals("X", it.next());
		assertFalse(it.hasNext());
	}
	
	/**
	 * Teste com vários tokens. Enquanto houver tokens, hasNext() deve retornar
	 * true e next() deve retornar o token.
	 */
	public void testVariosTokens() {
		String[] tokens = new String[] { "X", "Y", "Z" };
		ExpressaoIterator it = new ExpressaoIterator(tokens);
		assertTrue(it.hasNext());
		assertEquals("X", it.next());
		assertTrue(it.hasNext());
		assertEquals("Y", it.next());
		assertTrue(it.hasNext());
		assertEquals("Z", it.next());
		assertFalse(it.hasNext());
	}
	
	
	
}
