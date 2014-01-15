/*
 * DigitoVerificadorTest.java
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
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *
 *    Classe de testes para os cálculos do dígito verificador
 *
 * @author jadson
 * @since 28/10/2009
 * @version 1.0 criacao da classe
 *
 */
public class DigitoVerificadorTest extends TestCase {

	/**
	 * Test method for {@link br.ufrn.sigaa.biblioteca.util.BibliotecaUtil#geraDigitoVerificadorModulo11(java.lang.String)}.
	 */
	public void testGeraDigitoVerificadorModulo11() {
		
		try {
			String digito = BibliotecaUtil.geraDigitoVerificadorModulo11("0185200005");
			assertEquals(5, Integer.parseInt(digito));
		} catch (NegocioException e) {
			fail("Código não é numérico");
		}
	}

	
	/**
	 * Test method for {@link br.ufrn.sigaa.biblioteca.util.BibliotecaUtil#geraDigitoVerificadorModulo11(java.lang.String)}.
	 */
	public void testGeraDigitoVerificadorModulo11ComErro() {
		
		try {
			String digito = BibliotecaUtil.geraDigitoVerificadorModulo11("0185200005A");
			assertEquals(5, Integer.parseInt(digito));
		} catch (NegocioException e) {
			fail("Código não é numérico");
		}
	}
	
	
	/**
	 * Test method for {@link br.ufrn.sigaa.biblioteca.util.BibliotecaUtil#geraDigitoVerificadorUsandoAFuncaoDaFGV(java.lang.String)}.
	 */
	public void testGeraDigitoVerificadorUsandoOsCalculosDaFGV() {
		
		
		String digito = BibliotecaUtil.geraDigitoVerificadorUsandoAFuncaoDaFGV("RN00068521");
		assertEquals(9, Integer.parseInt(digito));

	
		digito = BibliotecaUtil.geraDigitoVerificadorUsandoAFuncaoDaFGV("RN00068522");
		assertEquals(7, Integer.parseInt(digito));
		
		
		digito = BibliotecaUtil.geraDigitoVerificadorUsandoAFuncaoDaFGV("RN00068630");
		assertEquals(4, Integer.parseInt(digito));
	
		
		digito = BibliotecaUtil.geraDigitoVerificadorUsandoAFuncaoDaFGV("RN00068844");
		assertEquals(7, Integer.parseInt(digito));
		
		digito = BibliotecaUtil.geraDigitoVerificadorUsandoAFuncaoDaFGV("RN00069005");
		assertEquals(0, Integer.parseInt(digito));
		
		digito = BibliotecaUtil.geraDigitoVerificadorUsandoAFuncaoDaFGV("RN00068750");
		assertEquals(5, Integer.parseInt(digito));
		
		digito = BibliotecaUtil.geraDigitoVerificadorUsandoAFuncaoDaFGV("RN00068879");
		assertEquals(1, Integer.parseInt(digito));
		
		digito = BibliotecaUtil.geraDigitoVerificadorUsandoAFuncaoDaFGV("RN00069000");
		assertEquals(1, Integer.parseInt(digito));
		
		digito = BibliotecaUtil.geraDigitoVerificadorUsandoAFuncaoDaFGV("RN00069001");
		assertEquals(8, Integer.parseInt(digito));
		
		digito = BibliotecaUtil.geraDigitoVerificadorUsandoAFuncaoDaFGV("RN00068968");
		assertEquals(0, Integer.parseInt(digito));      
		
	}
	
	
}

