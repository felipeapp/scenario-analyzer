/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 12/11/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.testes;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.DadosTabelaCutter;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;

/**
 *
 * <p>Testa a verificação da palavra cutter mais próxima </p>
 *
 * 
 * @author jadson
 *
 */
public class TestaPalavraMaisProximaCutter {

	@Test
	public void test() {
		
		List<DadosTabelaCutter> teste = new ArrayList<DadosTabelaCutter>();
		teste.add(new DadosTabelaCutter(1, 'S', 123, "A", 'T'));
		teste.add(new DadosTabelaCutter(2, 'S', 123, "Sandr", 'T'));
		teste.add(new DadosTabelaCutter(3, 'S', 123, "Santag", 'T'));
		teste.add(new DadosTabelaCutter(4, 'S', 123, "Sangr", 'T'));
		teste.add(new DadosTabelaCutter(5, 'S', 123, "Sani", 'T'));
		teste.add(new DadosTabelaCutter(6, 'S', 123, "Salgo", 'T'));
		teste.add(new DadosTabelaCutter(7, 'S', 123, "Santar", 'T'));
		teste.add(new DadosTabelaCutter(8, 'S', 123, "Sante", 'T'));
		teste.add(new DadosTabelaCutter(9, 'S', 123, "Santi", 'T'));
		teste.add(new DadosTabelaCutter(10, 'S', 123, "Santo", 'T')); // esse aqui é o que é para ser encontrado
		teste.add(new DadosTabelaCutter(11, 'S', 123, "Santis", 'T'));
		teste.add(new DadosTabelaCutter(12, 'S', 123, "Sao", 'T'));
		teste.add(new DadosTabelaCutter(13, 'S', 123, "Sap", 'T'));
		teste.add(new DadosTabelaCutter(14, 'S', 123, "Saq", 'T'));
		teste.add(new DadosTabelaCutter(15, 'S', 123, "Sar", 'T'));
		teste.add(new DadosTabelaCutter(16, 'S', 123, "Parto", 'T'));
		
	
		Assert.assertEquals(10, CatalogacaoUtil.calculaCodigoMaisProximo(teste, "Santos"));
	}
	
}
