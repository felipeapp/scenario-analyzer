/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 15/10/2010
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jfree.data.category.DefaultCategoryDataset;

import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import de.laures.cewolf.DatasetProduceException;
import de.laures.cewolf.DatasetProducer;

/**
 * Classe que gera o conjunto de dados utilizado para gerar o gráfico no relatório de
 * Crescimento de Periódicos por Área do CNPq num período dado.
 *
 * @author Felipe Rivas
 */
public class GraficoRelatorioCrescimentoPeriodicosPorCNPqPeriodo implements
		DatasetProducer {
	
	/** Timeout do cache do gráfico. */
	private static final int TIMEOUT = 5000; // ms

	@Override
	public String getProducerId() {
		return "graficoRelatorioCrescimentoPeriodicosPorCNPqPeriodo";
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
		List<AreaConhecimentoCnpq> areas = (List<AreaConhecimentoCnpq>) params.get("areas");
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		int cont = 1;
		
		for ( AreaConhecimentoCnpq area : areas ) {

			long a =  antes.get( area.getSigla() ) != null ? (Long) antes.get( area.getSigla() ) : 0;
			long d = depois.get( area.getSigla() ) != null ? (Long) depois.get( area.getSigla() ) : 0;
			
			dataset.addValue(a, "Quantidade anterior", cont + " - " + area.getSigla());
			dataset.addValue(d - a, "Crescimento no Período", cont + " - " + area.getSigla());
			
			cont++;
			
		}
		return dataset;
	}

}
