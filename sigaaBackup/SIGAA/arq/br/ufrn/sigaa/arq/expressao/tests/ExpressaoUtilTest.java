/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/13 - 19:27:18
 */
package br.ufrn.sigaa.arq.expressao.tests;

import java.util.List;

import junit.framework.TestCase;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;

/**
 * Testes unitários para métodos utilitários de expressões
 * do SIGAA
 * @author David Pereira
 *
 */
public class ExpressaoUtilTest extends TestCase {

	public void testExpressaoVaziaToTokens() {
		List<String> tokens = ExpressaoUtil.expressaoToTokens("");
		assertTrue(tokens.isEmpty());
	}
	
	public void testExpressaoNullToTokens() {
		try {
			ExpressaoUtil.expressaoToTokens(null);
			fail("Deveria ter disparado IllegalArgumentException");
		} catch(IllegalArgumentException e) {
			
		}
	}
	
	public void testExpressao1ToTokens() {
		List<String> tokens = ExpressaoUtil.expressaoToTokens("(1 OU 2 E 3)");
		assertNotNull(tokens);
		assertFalse(tokens.isEmpty());
		assertEquals(7, tokens.size());
	}

	public void testExpressao2ToTokens() {
		List<String> tokens = ExpressaoUtil.expressaoToTokens("(1 OU 2) E (2 OU 3) E (3 OU 4)");
		assertNotNull(tokens);
		assertFalse(tokens.isEmpty());
		assertEquals(17, tokens.size());
	}
	
	public void testExpressao3ToTokens() {
		List<String> tokens = ExpressaoUtil.expressaoToTokens("(((1 OU 2 E 3) OU 4) E (((5 OU 1) E 4) OU 3))");
		assertNotNull(tokens);
		assertFalse(tokens.isEmpty());
		assertEquals(27, tokens.size());
	}
	
}
