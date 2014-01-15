/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 15/10/2010
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.jfree.data.category.DefaultCategoryDataset;

import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;

/**
 * Classe que gera o conjunto de dados utilizado para gerar o gráfico no relatório de
 * Crescimento de Periódicos por Classe num período dado.
 *
 * @author Bráulio
 */
public class GraficoRelatorioCrescimentoPeriodicosPorClassePeriodo implements DatasetProducer {
	
	/** Timeout do cache do gráfico. */
	private static final int TIMEOUT = 5000; // ms

	@Override
	public String getProducerId() {
		return "graficoRelatorioCrescimentoPeriodicosPorClassePeriodo";
	}

	@Override
	public boolean hasExpired( @SuppressWarnings("rawtypes") Map params, Date data ) {
		return (System.currentTimeMillis() - data.getTime()) >= TIMEOUT;
	}

	@Override
	public Object produceDataset( @SuppressWarnings("rawtypes") Map params ) throws DatasetProduceException {
		
		@SuppressWarnings("unchecked")
		Map<String, Long> antes = (Map<String, Long>) params.get("antes");
		
		@SuppressWarnings("unchecked")
		Map<String, Long> depois = (Map<String, Long>) params.get("depois");
		
		@SuppressWarnings("unchecked")
		Set<String> classes = (Set<String>) params.get("classes");
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		for ( String classe : classes ) {

			//int a = antes.get(classe) != null ? antes.get(classe)[1] + antes.get(classe)[3] : 0;
			//int d = depois.get(classe) != null ? depois.get(classe)[1] + depois.get(classe)[3] : 0;
			long a = antes.get(classe) != null ? antes.get(classe) : 0;
			long d = depois.get(classe) != null ? depois.get(classe) : 0;
			
			dataset.addValue(a, "Quantidade anterior", classe);
			dataset.addValue(d - a, "Crescimento no Período", classe);
			
		}
		return dataset;
	}

}
