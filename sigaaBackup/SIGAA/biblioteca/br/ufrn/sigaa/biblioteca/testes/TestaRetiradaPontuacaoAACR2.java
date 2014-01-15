/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 18/03/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.testes;

import junit.framework.TestCase;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;

/**
 * <p>Classe para testar se o método <code>getDadoSemFormatacaoAACR2</code> faz o que é esperado. </p>
 * 
 * @author jadson
 *
 */
public class TestaRetiradaPontuacaoAACR2 extends TestCase {

	/**
	 * Test method for {@link br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo#getDadoSemFormatacaoAACR2()}.
	 */
	public void testGetDadoSemFormatacaoAACR2() {
		SubCampo sub = new SubCampo();
		
		assertEquals(null, sub.getDadoSemFormatacaoAACR2());
		
		sub.setDado("4.ed. - ");
		assertEquals("4.ed. ", sub.getDadoSemFormatacaoAACR2());
		System.out.println(sub.getDado());
		System.out.println(sub.getDadoSemFormatacaoAACR2());
		
		sub.setDado("Renalle Diniz Costa Gurgel; Orientador José Arimatés de Oliveira.- ");
		
		assertEquals("Renalle Diniz Costa Gurgel; Orientador José Arimatés de Oliveira.", sub.getDadoSemFormatacaoAACR2());
		
		sub.setDado("Nordeste -");
		
		assertEquals("Nordeste ", sub.getDadoSemFormatacaoAACR2());
		System.out.println(sub.getDado());
		System.out.println(sub.getDadoSemFormatacaoAACR2());
		
		sub.setDado("um testemunho sobre o nordeste brasileiro / ");
		
		assertEquals("um testemunho sobre o nordeste brasileiro ", sub.getDadoSemFormatacaoAACR2());
		System.out.println(sub.getDado());
		System.out.println(sub.getDadoSemFormatacaoAACR2());
		
		sub.setDado("O primeiro passo : ");
		
		assertEquals("O primeiro passo ", sub.getDadoSemFormatacaoAACR2());
		System.out.println(sub.getDado());
		System.out.println(sub.getDadoSemFormatacaoAACR2());
		
		sub.setDado("");
		
		assertEquals("", sub.getDadoSemFormatacaoAACR2());
		
		sub.setDado("                           ");
	
		assertEquals("                           ", sub.getDadoSemFormatacaoAACR2());
		
	}

}
