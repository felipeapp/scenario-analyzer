/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 08/12/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.testes;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.Test;

import br.ufrn.sigaa.biblioteca.util.MultaUsuarioBibliotecaUtil;

/**
 * <p> Classe de Testes para o método que extrai o valor da multa da biblioteca a partir do parâmetro configurado pelo usuário.</p>
 *
 * 
 * @author jadson
 *
 */
public class ExtraiValorMultaTest {

	
	/**
	 * O valor do parâmetro da multa no sistema é informado no padrão brasileiro "1.000.000,00". Mas para gerar o valor numérico correto da multa
	 * precisa formatar a string de acordo com o padrão americano: "1000000.00".
	 * 
	 * Test method for {@link br.ufrn.sigaa.biblioteca.util.MultaUsuarioBibliotecaUtil#extraiValorNumeroMulta(java.lang.String)}.
	 */
	@Test
	public void testExtraiValorNumeroMulta() {
		BigDecimal valor = MultaUsuarioBibliotecaUtil.extraiValorNumeroMulta("0,50");
		assertTrue(valor.equals(new BigDecimal("0.50")));
		
		BigDecimal valor2 = MultaUsuarioBibliotecaUtil.extraiValorNumeroMulta("1.000,00");
		assertTrue(valor2.equals(new BigDecimal("1000.00")));
		
		BigDecimal valor3 = MultaUsuarioBibliotecaUtil.extraiValorNumeroMulta("5.999.999,99");
		assertTrue(valor3.equals(new BigDecimal("5999999.99")));
	}

}
