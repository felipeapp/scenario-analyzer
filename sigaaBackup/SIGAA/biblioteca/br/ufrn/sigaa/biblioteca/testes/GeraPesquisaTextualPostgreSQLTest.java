/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 09/03/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.testes;

import junit.framework.TestCase;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextualPosgreSQL;

/**
 * <p>Testa a geração do mecanismo de pesquisa para o PostgreSQL</p>
 * 
 * @author jadson
 *
 */
public class GeraPesquisaTextualPostgreSQLTest extends TestCase {

	/**
	 * Test method for {@link br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextualPosgreSQL#gerarPesquisaTextual(java.lang.String, java.lang.String[])}.
	 */
	public void testGerarMecaminismoPesquisaTextual() {
		
		GeraPesquisaTextualPosgreSQL gerador = new GeraPesquisaTextualPosgreSQL();
		
		String mecanismoPesquisa = gerador.gerarMecanismoPesquisaTextual("titulo_ascii");
	
		System.out.println(mecanismoPesquisa.trim());
		            
		assertEquals(" to_tsvector('portuguese', titulo_ascii) @@ to_tsquery( ? ) ".trim(), mecanismoPesquisa.trim());
		
	}

	
	/**
	 * Test method for {@link br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextualPosgreSQL#gerarPesquisaTextual(java.lang.String, java.lang.String[])}.
	 */
	public void testFormataTextoPesquisaTextual() {
		
		GeraPesquisaTextualPosgreSQL gerador = new GeraPesquisaTextualPosgreSQL();
		
		String textoFormatado = gerador.formataTextoPesquisaTextual(new String[]{"mach?do", "assis"});
	
		System.out.println(textoFormatado.trim());
		            
		assertEquals(" '  MACH%DO  &  ASSIS  ' ".trim(), textoFormatado.trim());
		
	}
	
}
