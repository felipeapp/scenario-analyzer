/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 23/04/2009
 */
package br.ufrn.arq.tests;

import junit.framework.TestCase;
import br.ufrn.arq.util.StringUtils;

/**
 * Testes unitários para a classe StringUtils.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class StringUtilsTest extends TestCase {

	public void testToAsciiHtml() {
		String ascii = StringUtils.toAsciiHtml("<p>Acentua&ccedil;&atilde;o</p>");
		assertEquals("<p>Acentuacao</p>", ascii);
		
		ascii = StringUtils.toAsciiHtml("<p>Mais um teste com acentua&ccedil;&atilde;o</p>");
		assertEquals("<p>Mais um teste com acentuacao</p>", ascii);
		
		ascii = StringUtils.toAsciiHtml("<p>Edital de convoca&ccedil;&atilde;o para os novos servidores da UFRN acessarem o boletim de servi&ccedil;o para novidades do SIPAC.</p>");
		assertEquals("<p>Edital de convocacao para os novos servidores da UFRN acessarem o boletim de servico para novidades do SIPAC.</p>", ascii);
		
		ascii = StringUtils.toAsciiHtml("MINIST&Eacute;RIO DA EDUCA&Ccedil;&Atilde;O");
		assertEquals("MINISTERIO DA EDUCACAO", ascii);
		
		ascii = StringUtils.toAsciiHtml("atribui&ccedil;&atilde;o par&aacute;grafos JOS&Eacute; BRAZ DINIZ FILHO, matr&iacute;cula exerc&iacute;cio");
		assertEquals("atribuicao paragrafos JOSE BRAZ DINIZ FILHO, matricula exercicio", ascii);
		
		ascii = StringUtils.toAsciiHtml("&aacute;&ecirc;&igrave;&otilde;&uuml;&nbsp;&Aacute;&Ecirc;&Igrave;&Otilde;&Uuml;");
		assertEquals("aeiou AEIOU", ascii);
		
	}
	
}
