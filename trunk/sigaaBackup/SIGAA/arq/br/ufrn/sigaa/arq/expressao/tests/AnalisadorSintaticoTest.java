/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/13 - 19:27:36
 */
package br.ufrn.sigaa.arq.expressao.tests;

import java.util.Stack;

import junit.framework.TestCase;
import br.ufrn.sigaa.arq.expressao.AnalisadorSintatico;
import br.ufrn.sigaa.arq.expressao.ExpressaoIterator;

/**
 * Testes unitários para o analisador sintático de expressões
 * do SIGAA
 * 
 * @author David Pereira
 *
 */
public class AnalisadorSintaticoTest extends TestCase {

	public void testExpressaoVazia() {
		AnalisadorSintatico as = new AnalisadorSintatico(new ExpressaoIterator(new String[0]));
		Stack<String> npr = as.analizar();
		assertNotNull(npr);
		assertTrue(npr.empty());
	}
	
	public void testExpressaoNull() {
		AnalisadorSintatico as = new AnalisadorSintatico(new ExpressaoIterator((String[]) null));
		Stack<String> npr = as.analizar();
		assertNotNull(npr);
		assertTrue(npr.empty());
	}
	
	public void testExpressao1() {
		String a = "( 1 ) OU ( 2 ) E ( 3 )";
		AnalisadorSintatico as = new AnalisadorSintatico(a);
		Stack<String> npr = as.analizar();
		assertNotNull(npr);
		assertFalse(npr.empty());
		assertEquals(5, npr.size());
		assertEquals("[1, 2, 3, E, OU]", npr.toString());
	}

	public void testExpressao2() {
		String a = "(1 E 2) OU (2 E 3) OU (3 E 4)";
		AnalisadorSintatico as = new AnalisadorSintatico(a);
		Stack<String> npr = as.analizar();
		assertNotNull(npr);
		assertFalse(npr.empty());
		assertEquals(11, npr.size());
		assertEquals("[1, 2, E, 2, 3, E, OU, 3, 4, E, OU]", npr.toString());
	}
	
	public void testExpressao3() {
		String a = "(1) OU (1 E 3) OU (4) OU (5)";
		AnalisadorSintatico as = new AnalisadorSintatico(a);
		Stack<String> npr = as.analizar();
		assertNotNull(npr);
		assertFalse(npr.empty());
		assertEquals(9, npr.size());
		assertEquals("[1, 1, 3, E, OU, 4, OU, 5, OU]", npr.toString());
	}
	
	public void testExpressao4() {
		String a = "(1 E (2 OU 3)) OU (4) OU (5) OU (6)";
		AnalisadorSintatico as = new AnalisadorSintatico(a);
		Stack<String> npr = as.analizar();
		assertNotNull(npr);
		assertFalse(npr.empty());
		assertEquals(11, npr.size());
		assertEquals("[1, 2, 3, OU, E, 4, OU, 5, OU, 6, OU]", npr.toString());
	}
	
	public void testExpressao5() {
		String a = "(((1 OU 2 E 3) OU 4) E (((5 OU 1) E 4) OU 3))";
		AnalisadorSintatico as = new AnalisadorSintatico(a);
		Stack<String> npr = as.analizar();
		assertNotNull(npr);
		assertFalse(npr.empty());
		assertEquals(15, npr.size());
		assertEquals("[1, 2, 3, E, OU, 4, OU, 5, 1, OU, 4, E, 3, OU, E]", npr.toString());
	}
	
}
