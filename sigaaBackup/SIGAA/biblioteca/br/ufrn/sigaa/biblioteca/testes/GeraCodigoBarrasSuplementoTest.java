/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 06/03/2013
 * 
 */
package br.ufrn.sigaa.biblioteca.testes;

import org.junit.Assert;
import org.junit.Test;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 * Testa os métodos de geração de letras para os código de barras dos anexos e suplementos.
 *
 * @author jadson - jadson@info.ufrn.br
 * @vesion 1.0 - criação da classe.
 * @since 06/03/2013
 *
 */
public class GeraCodigoBarrasSuplementoTest {


	@Test
	public void testCaraterCorespondente() {
		try {
			Assert.assertEquals(BibliotecaUtil.geraCaraterCorespondente(0), "A");
			Assert.assertEquals(BibliotecaUtil.geraCaraterCorespondente(1), "B");
			Assert.assertEquals(BibliotecaUtil.geraCaraterCorespondente(2), "C");
		} catch (NegocioException e) {
			assert false;
		}
	}
	
	@Test
	public void testValorCorespondente() {
		
		Assert.assertEquals(BibliotecaUtil.geraValorCorespondente('A'), 0);
		Assert.assertEquals(BibliotecaUtil.geraValorCorespondente('B'), 1);
		Assert.assertEquals(BibliotecaUtil.geraValorCorespondente('C'), 2);
		
		
		// testa que as função são oposta uma da outra
		int valor = 5;
		
		try {
			Assert.assertEquals( BibliotecaUtil.geraValorCorespondente(BibliotecaUtil.geraCaraterCorespondente(valor).charAt(0)), valor);
		} catch (NegocioException e) {
			assert false;
		}
		
	}
	
}
