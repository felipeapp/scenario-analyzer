/*
 * SeparacaoTextoBuscaTest.java
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
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *
 *     Realiza o teste do método que forma as palavras de busca usadas na busca de títulos.
 *
 * @author jadson
 * @since 06/08/2009
 * @version 1.0 criacao da classe
 *
 */
public class SeparacaoTextoBuscaTest extends TestCase {

	/**
	 * Test method for {@link br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaUtil#retornaPalavrasBusca(java.lang.String)}.
	 */
	public void testRetornaPalavrasBusca() {
		String[] temp = BibliotecaUtil.retornaPalavrasBusca("teste teste2 \"teste sentença\"");
		
		for (int i = 0; i < temp.length; i++) {
			System.out.println(temp);
		}
		
		assertTrue(temp.length == 3 );
		
	}

	
	
	public static void main(String[] args) {
		
		String[] temp = BibliotecaUtil.retornaPalavrasBusca("teste teste2 \"teste sentença\"");
		
		for (int i = 0; i < temp.length; i++) {
			System.out.println("|"+temp[i]+"|");
		}
		
		System.out.println("-------------------------");
		
		String[] temp2 = BibliotecaUtil.retornaPalavrasBusca("teste teste2 \"teste sentença");
		
		for (int i = 0; i < temp2.length; i++) {
			System.out.println("|"+temp2[i]+"|");
		}
		
		System.out.println("-------------------------");
		
		String[] temp3 = BibliotecaUtil.retornaPalavrasBusca("teste teste2 \"teste sentença\" \"string java\" \"yyyyyy  xxxxx");
		
		for (int i = 0; i < temp3.length; i++) {
			System.out.println("|"+temp3[i]+"|");
		}
		
		System.out.println("-------------------------");
		
		String[] temp4 = BibliotecaUtil.retornaPalavrasBusca(null);
		
		for (int i = 0; i < temp4.length; i++) {
			System.out.println("|"+temp4[i]+"|");
		}
		
		
		
		System.out.println("-------------------------");
		
		String[] temp5 = BibliotecaUtil.retornaPalavrasBusca(" o teste de preprosicao  por    ");
		
		for (int i = 0; i < temp5.length; i++) {
			System.out.println("|"+temp5[i]+"|");
		}
		
		
		
	}
}
