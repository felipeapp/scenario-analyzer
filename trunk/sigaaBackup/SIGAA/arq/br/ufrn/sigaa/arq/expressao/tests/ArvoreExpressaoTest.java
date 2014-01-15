/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/08/13 - 19:27:10
 */
package br.ufrn.sigaa.arq.expressao.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;

/**
 * Testes unitários para árvore de expressões
 * do SIGAA
 * @author David Pereira
 *
 */
public class ArvoreExpressaoTest extends TestCase {
	
	public void testExpressaoVazia() {
		ArvoreExpressao arvore = ArvoreExpressao.fromExpressao("");
		assertNull(arvore);
	}
	
	public void testExpressaoNull() {
		ArvoreExpressao arvore = ArvoreExpressao.fromExpressao(null);
		assertNull(arvore);
	}
	
	public void testExpressaoInvalida() {
		try {
			ArvoreExpressao.fromExpressao("( ( 21736 E 21692 ) ) ) ( ( ( OU ) ( 25261 E 24755 E 21668 ) ) ) ) ) ) ) ) ");
			fail();
		} catch(Exception e) { }
	}
	
	public void testExpressaoInvalida2() {
		try {
			ArvoreExpressao.fromExpressao("( ( 21736 E 21692 ) ) OU ( 25261 ) E ( 24755 ) E 21668 )");
			fail();
		} catch(Exception e) { }
	}
	
	public void testExpressao1() {
		ArvoreExpressao arvore = ArvoreExpressao.fromExpressao("(1 OU 2 E 3)");
		assertNotNull(arvore);
		
		List<Integer> comps = new ArrayList<Integer>();
		comps.add(3);
		assertFalse(arvore.eval(comps));
		
		comps.clear();
		arvore = ArvoreExpressao.fromExpressao("(1 OU 2 E 3)");
		
		comps.add(1);
		assertTrue(arvore.eval(comps));
		assertEquals(Integer.valueOf(1), arvore.getMatches().get(0));
		assertEquals("( 1  OU )", arvore.getMatchExpression());
		
		comps.clear();
		
		comps.add(2);
		arvore = ArvoreExpressao.fromExpressao("(1 OU 2 E 3)");
		assertFalse(arvore.eval(comps));
		comps.add(3);
		arvore = ArvoreExpressao.fromExpressao("(1 OU 2 E 3)");
		assertTrue(arvore.eval(comps));
		assertEquals(Integer.valueOf(2), arvore.getMatches().get(0));
		assertEquals(Integer.valueOf(3), arvore.getMatches().get(1));
		comps.add(1);
		arvore = ArvoreExpressao.fromExpressao("(1 OU 2 E 3)");
		assertTrue(arvore.eval(comps));
		assertEquals(Integer.valueOf(1), arvore.getMatches().get(0));
	}
	
	public void testExpressao2() {
		ArvoreExpressao arvore = ArvoreExpressao.fromExpressao("((1 OU 2) E 3)");
		assertNotNull(arvore);
		
		List<Integer> comps = new ArrayList<Integer>();
		comps.add(3);
		assertFalse(arvore.eval(comps));
		
		comps.clear();
		
		comps.add(1);
		comps.add(3);
		assertTrue(arvore.eval(comps));
		assertEquals(Integer.valueOf(1), arvore.getMatches().get(0));
		assertEquals(Integer.valueOf(3), arvore.getMatches().get(1));
		assertEquals("(( 1  OU  2 ) E  3 )", arvore.getMatchExpression());
		
		comps.clear();
		
		comps.add(2);
		assertFalse(arvore.eval(comps));
		comps.add(3);
		assertTrue(arvore.eval(comps));
		comps.add(1);
		assertTrue(arvore.eval(comps));
	}
	
	public void testExpressao3() {
		ArvoreExpressao arvore = ArvoreExpressao.fromExpressao("(1 E 2) OU (2 E 3) OU (3 E 4)");
		assertNotNull(arvore);
		
		List<Integer> comps = new ArrayList<Integer>();
		comps.add(3);
		assertFalse(arvore.eval(comps));
		
		comps.clear();
		
		comps.add(2);
		comps.add(3);
		assertTrue(arvore.eval(comps));
		assertEquals(Integer.valueOf(2), arvore.getMatches().get(0));
		assertEquals(Integer.valueOf(3), arvore.getMatches().get(1));
		assertEquals("(( OU ( 2  E  3 )) OU )", arvore.getMatchExpression());
		
		comps.clear();
		
		comps.add(2);
		assertFalse(arvore.eval(comps));
		comps.add(3);
		assertTrue(arvore.eval(comps));
		comps.add(1);
		assertTrue(arvore.eval(comps));
		comps.add(4);
		assertTrue(arvore.eval(comps));
	}
	
	public void testExpressao4() {
		ArvoreExpressao arvore = ArvoreExpressao.fromExpressao("(((1 OU 2 E 3) OU 4) E (((5 OU 1) E 4) OU 3))");
		assertNotNull(arvore);
		
		List<Integer> comps = new ArrayList<Integer>();
		comps.add(3);
		assertFalse(arvore.eval(comps));
		
		comps.clear();
		
		comps.add(1);
		comps.add(4);
		assertTrue(arvore.eval(comps));
		assertEquals(Integer.valueOf(1), arvore.getMatches().get(0));
		assertEquals("((( 1  OU ) OU  4 ) E ((( 5  OU  1 ) E  4 ) OU  3 ))", arvore.getMatchExpression());
		
		comps.clear();
		
		comps.add(2);
		assertFalse(arvore.eval(comps));
		comps.add(3);
		assertTrue(arvore.eval(comps));
		comps.add(1);
		assertTrue(arvore.eval(comps));
	}
	
	public void testExpressao5() {
		ArvoreExpressao arvore = ArvoreExpressao.fromExpressao("(1 E (2 OU 3)) OU (4) OU (5) OU (6)");
		assertNotNull(arvore);
		
		List<Integer> comps = new ArrayList<Integer>();
		comps.add(3);
		assertFalse(arvore.eval(comps));
		
		comps.clear();
		
		comps.add(1);
		comps.add(3);
		assertTrue(arvore.eval(comps));
		assertEquals(Integer.valueOf(1), arvore.getMatches().get(0));
		assertEquals("(((( 1  E ( 2  OU  3 )) OU  4 ) OU  5 ) OU  6 )", arvore.getMatchExpression());
		
		comps.clear();
		
		comps.add(2);
		assertFalse(arvore.eval(comps));
		comps.add(4);
		assertTrue(arvore.eval(comps));
		comps.add(6);
		assertTrue(arvore.eval(comps));
	}
	
	public void testExpressao6() {
		try {
			ArvoreExpressao.fromExpressao("( E )");
			fail("Deveria falhar na expressão ( E )");
		} catch(Exception e) {
		}
	}
	
	public void testExpressao7() {
		try {
			ArvoreExpressao.fromExpressao("( )");
			fail("Deveria falhar na expressão ( )");
		} catch(Exception e) {
		}
	}

	public void testSingleMatch() {
		
		String expressao = "( 20027 E 12345 ) OU ( 23513 ) OU ( 23573 ) OU ( 23536 ) OU ( 23500 E 23501 ) OU ( 23587 ) OU ( 23582 )";
		ArvoreExpressao arvore = ArvoreExpressao.fromExpressao(expressao);
		Integer[] componentes = arvore.componentesIsolados();
		assertEquals(23513, componentes[0].intValue());
		assertEquals(23573, componentes[1].intValue());
		assertEquals(23536, componentes[2].intValue());
		assertEquals(23587, componentes[3].intValue());
		assertEquals(23582, componentes[4].intValue());
	}
	
	/*
	public void testExpressoesEquivalenciaBanco() throws Exception {
		evaluateExpressions("select codigo, equivalencia as expressao from ensino.componente_curricular_detalhes where equivalencia is not null and equivalencia != ''");
	}
	
	public void testExpressoesPreRequisitoBanco() throws Exception {
		evaluateExpressions("select codigo, pre_requisito as expressao from ensino.componente_curricular_detalhes where pre_requisito is not null and pre_requisito != ''");
	}
	
	public void testExpressoesCoRequisitoBanco() throws Exception {
		evaluateExpressions("select codigo, co_requisito as expressao from ensino.componente_curricular_detalhes where co_requisito is not null and co_requisito != ''");
	}

	@SuppressWarnings("unchecked")
	private void evaluateExpressions(String consulta) throws Exception {
		StringBuilder erros = new StringBuilder();
		
		DataSource ds = getDatasource();
		List<Map<String, Object>> expressoes = new JdbcTemplate(ds).queryForList(consulta);
		for (Map<String, Object> expressao : expressoes) {
			try {
				ArvoreExpressao.fromExpressao(expressao.get("expressao").toString());
			} catch(Exception e) {
				erros.append(expressao.get("codigo") + ": " + expressao.get("expressao") + "\n");
			}
		}
		
		if (erros.length() > 0)
			fail(erros.toString());
	}

	private DataSource getDatasource() throws Exception {
		Properties cfg = new Properties();
		cfg.put("driverClassName", "org.postgresql.Driver");
		cfg.put("url", "jdbc:postgresql://desenvolvimento.info.ufrn.br:5432/sigaa");
		cfg.put("username", "sigaa");
		cfg.put("password", "sigaa");
		DataSource ds = BasicDataSourceFactory.createDataSource(cfg);
		return ds;
	}
*/	
}
